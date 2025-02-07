package pe.edu.vallegrande.vg_ms_attorney.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document(collection = "attorney")
public class Attorney {
    @Id
    private String idAttorney;
    private String uid;
    private String names;
    private String surnames;
    private String sex;
    private String birth_date;
    private String baptism;
    private String first_Communion;
    private String confirmation;
    private String marriage;
    private String relationship;
    private String role;
    private String email;
    private String password;
    private String cellphone;
    private String address;
    private String documentType;
    private String documentNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
}
