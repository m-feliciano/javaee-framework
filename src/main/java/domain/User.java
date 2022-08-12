package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.enums.Perfil;
import lombok.*;
import org.hibernate.Hibernate;
import servlets.utils.EncryptDecrypt;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PERFIS")
    private final Set<Integer> perfis = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;
    @Column(name = "login")
    private String login;
    @JsonIgnore
    @Column(name = "password")
    private String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.addPerfil(Perfil.CLIENT);
    }

    public void addPerfil(Perfil perfil) {
        perfis.add(perfil.getCod());
    }

    public Set<Perfil> getPerfils() {
        return perfis.stream().map(Perfil::toEnum).collect(Collectors.toSet());
    }

    public boolean equals(String login, String password) {
        return this.login.equals(login) && Objects.equals(EncryptDecrypt.decrypt(this.password), password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
