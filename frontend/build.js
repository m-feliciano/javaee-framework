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
const pageScripts = new Set(
    ["csrf-util.js", "pretty-json.js", "inspect.js", "inspect-raw.js", "navbar.js", "design-system.js"]
);

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

const cssFiles = listFiles(cssDir, ".css");
const allJs = listFiles(jsDir, ".js");
const mainJs = allJs.filter(f => !pageScripts.has(path.basename(f)));
let tmp = "";
cssFiles.forEach(f => tmp += `import "${relImport(f)}";\n`);
mainJs.forEach(f => tmp += `import "${relImport(f)}";\n`);
fs.writeFileSync(tmpEntry, tmp, "utf8");

(async () => {
    try {
        ensureDir(outDir);
        await esbuild.build({
            entryPoints: [tmpEntry],
            bundle: true,
            minify: true,
            sourcemap: false,
            outfile: path.join(outDir, "main.js"),
            loader: {".js": "js", ".css": "css"},
            logLevel: "silent"
        });
        const pageOutputs = {};
        for (const file of pageScripts) {
            const input = path.join(jsDir, file);
            if (!fs.existsSync(input)) continue;
            const base = path.basename(file, ".js");
            const out = path.join(outDir, `${base}.js`);
            await esbuild.build({
                entryPoints: [input],
                bundle: true,
                minify: true,
                sourcemap: false,
                outfile: out,
                loader: {".js": "js"},
                logLevel: "silent"
            });
            pageOutputs[file] = out;
        }

        const manifest = {};
        const mainJsOut = path.join(outDir, "main.js");

        if (fs.existsSync(mainJsOut)) {
            manifest["main.js"] = renameWithHash(mainJsOut, "main");
        }

        const mainCssOut = path.join(outDir, "main.css");
        if (fs.existsSync(mainCssOut)) {
            manifest["main.css"] = renameWithHash(mainCssOut, "main");
        }

        for (const [orig, outPath] of Object.entries(pageOutputs)) {
            if (fs.existsSync(outPath)) {
                const base = path.basename(orig, ".js");
                const newName = renameWithHash(outPath, base);
                manifest[orig] = newName;
            }
        }
        fs.writeFileSync(manifestPath, JSON.stringify(manifest, null, 2), "utf8");

    } catch (err) {
        console.error("Build falhou:", err);
        process.exit(1);
    } finally {
        try {
            fs.unlinkSync(tmpEntry);
        } catch {
        }
    }
})();
