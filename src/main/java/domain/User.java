package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import domain.enums.Perfil;
import domain.enums.Status;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_perfis", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "perfis")
    private final Set<Integer> perfis = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private Long id;
    @Column(name = "login", unique = true)
    private String login;
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @ToString.Exclude
    private String status;

    @Column(name = "image_url", columnDefinition = "TEXT")
    @ToString.Exclude
    private String imgUrl;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.setStatus(Status.ACTIVE.getDescription());
        this.addPerfil(Perfil.CLIENT);
    }

    public User(Long id, String login) {
        this.id = id;
        this.login = login;
    }

    public User(Long id, String login, String imgUrl) {
        this.id = id;
        this.login = login;
        this.imgUrl = imgUrl;
    }

    public User(String login, String password, String imgUrl) {
        this.password = password;
        this.login = login;
        this.imgUrl = imgUrl;
    }

    public User(Long id, String login, Set<Integer> perfis) {
        this.id = id;
        this.login = login;
        this.perfis.addAll(perfis);
    }

    public void addPerfil(Perfil perfil) {
        perfis.add(perfil.getCod());
    }

    public void setPerfis(Set<Perfil> perfis) {
        this.perfis.clear();
        perfis.forEach(this::addPerfil);
    }

    public Set<Perfil> getPerfis() {
        return perfis.stream().map(Perfil::toEnum).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", perfis=" + getPerfis() +
                '}';
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
