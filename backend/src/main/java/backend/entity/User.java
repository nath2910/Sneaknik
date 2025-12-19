package backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users", schema = "public")  // adapte le nom de la table si besoin
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String first_name;

    @Column(length = 100, nullable = false)
    private String last_name;

    @Column(length = 200, nullable = true)
    private String email;

    @Column(length = 200, nullable = true)
    private String password;

    // ====== CONSTRUCTEURS ======
    public User() {}

     // ===== GETTERS / SETTERS =====

    public Long getId(){
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
}
