const esbuild = require("esbuild");
const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const root = path.join(path.join(__dirname, ".."), "src/main/webapp");
const res = path.join(root, "resources");
const jsDir = path.join(res, "js");
const cssDir = path.join(res, "css");
const outDir = path.join(res, "dist");
const tmpEntry = path.join(__dirname, ".build-entry.js");
const manifestPath = path.join(outDir, "manifest.json");

const pageScripts = new Set([
    "csrf-util.js",
    "pretty-json.js",
    "inspect.js",
    "inspect-raw.js",
    "navbar.js",
    "design-system.js",
    "login.js",
    "health.js",
    "activity-timeline.js",
    "backButton.js"
]);

if (fs.existsSync(outDir)) {
    fs.rmSync(outDir, {recursive: true, force: true});
}

function ensureDir(dir) {
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, {recursive: true});
}

function hashFile(file) {
    const buf = fs.readFileSync(file);
    return crypto.createHash("sha256").update(buf).digest("hex").slice(0, 12);
}

function renameWithHash(file, baseName) {
    if (!fs.existsSync(file)) return null;
    const h = hashFile(file);
    const ext = path.extname(file);
    const dir = path.dirname(file);
    const newName = `${baseName}.${h}${ext}`;
    const newPath = path.join(dir, newName);
    fs.renameSync(file, newPath);
    return newName;
}

function relImport(p) {
    let r = path.relative(process.cwd(), p).replace(/\\/g, "/");
    if (!r.startsWith(".")) r = "./" + r;
    return r;
}

function listFiles(dir, ext) {
    if (!fs.existsSync(dir)) return [];
    return fs.readdirSync(dir)
        .filter(f => f.toLowerCase().endsWith(ext))
        .map(f => path.join(dir, f))
        .sort();
}

// Pega apenas main.css (que importa todos os outros)
const mainCssPath = path.join(cssDir, "main.css");
const allJs = listFiles(jsDir, ".js");
const mainJs = allJs.filter(f => !pageScripts.has(path.basename(f)));

// Cria entry point temporário
let tmp = "";
if (fs.existsSync(mainCssPath)) {
    tmp += `import "${relImport(mainCssPath)}";\n`;
}
mainJs.forEach(f => tmp += `import "${relImport(f)}";\n`);
fs.writeFileSync(tmpEntry, tmp, "utf8");

console.log("🚀 Building assets...");
console.log(`📦 CSS: ${fs.existsSync(mainCssPath) ? 'main.css' : 'none'}`);
console.log(`📦 JS scripts: ${mainJs.length} files`);
console.log(`📄 Page scripts: ${pageScripts.size} files`);

(async () => {
    try {
        ensureDir(outDir);

        // Build main bundle (CSS + JS comum)
        console.log("\n⚡ Building main bundle...");
        await esbuild.build({
            entryPoints: [tmpEntry],
            bundle: true,
            minify: true,
            sourcemap: false,
            outfile: path.join(outDir, "main.js"),
            loader: {".js": "js", ".css": "css"},
            logLevel: "warning",
            treeShaking: true,
            target: ['es2020'],
            format: 'esm',
        });

        // Build page scripts separadamente
        console.log("⚡ Building page scripts...");
        const pageOutputs = {};
        for (const file of pageScripts) {
            const input = path.join(jsDir, file);
            if (!fs.existsSync(input)) {
                console.log(`⚠️  Skipping ${file} (not found)`);
                continue;
            }
            const base = path.basename(file, ".js");
            const out = path.join(outDir, `${base}.js`);
            await esbuild.build({
                entryPoints: [input],
                bundle: true,
                minify: true,
                sourcemap: false,
                outfile: out,
                loader: {".js": "js"},
                logLevel: "warning",
                treeShaking: true,
                target: ['es2020'],
                format: 'esm',
            });
            pageOutputs[file] = out;
            console.log(`  ✓ ${file}`);
        }

        // Gera manifest com hashes
        console.log("\n🔒 Generating manifest with hashes...");
        const manifest = {};
        const mainJsOut = path.join(outDir, "main.js");

        if (fs.existsSync(mainJsOut)) {
            const hashedName = renameWithHash(mainJsOut, "main");
            manifest["main.js"] = hashedName;
            const size = fs.statSync(path.join(outDir, hashedName)).size;
            console.log(`  ✓ main.js → ${hashedName} (${(size/1024).toFixed(2)} KB)`);
        }

        const mainCssOut = path.join(outDir, "main.css");
        if (fs.existsSync(mainCssOut)) {
            const hashedName = renameWithHash(mainCssOut, "main");
            manifest["main.css"] = hashedName;
            const size = fs.statSync(path.join(outDir, hashedName)).size;
            console.log(`  ✓ main.css → ${hashedName} (${(size/1024).toFixed(2)} KB)`);
        }

        for (const [orig, outPath] of Object.entries(pageOutputs)) {
            if (fs.existsSync(outPath)) {
                const base = path.basename(orig, ".js");
                const newName = renameWithHash(outPath, base);
                manifest[orig] = newName;
                const size = fs.statSync(path.join(outDir, newName)).size;
                console.log(`  ✓ ${orig} → ${newName} (${(size/1024).toFixed(2)} KB)`);
            }
        }

        fs.writeFileSync(manifestPath, JSON.stringify(manifest, null, 2), "utf8");
        console.log(`\n✅ Build complete! Manifest: ${Object.keys(manifest).length} files`);

        // Calcula tamanho total
        const totalSize = Object.values(manifest).reduce((sum, fileName) => {
            const filePath = path.join(outDir, fileName);
            return sum + (fs.existsSync(filePath) ? fs.statSync(filePath).size : 0);
        }, 0);
        console.log(`📊 Total size: ${(totalSize/1024).toFixed(2)} KB`);

    } catch (err) {
        console.error("❌ Build failed:", err);
        process.exit(1);
    } finally {
        try {
            fs.unlinkSync(tmpEntry);
        } catch {
            // Ignore
        }
    }
})();
