package uk.gov.digital.ho.proving.income.feedback;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Access(AccessType.FIELD)
@NoArgsConstructor
@EqualsAndHashCode(of = "uuid")
class FeedbackEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid", nullable = false)
    @Getter
    private String uuid;

    @Column(name = "timestamp", nullable = false)
    @Getter
    private LocalDateTime timestamp;

    @Column(name = "session_id", nullable = false)
    @Getter
    private String sessionId;

    @Column(name = "deployment", nullable = false)
    @Getter
    private String deployment;

    @Column(name = "namespace", nullable = false)
    @Getter
    private String namespace;

    @Column(name = "user_id", nullable = false)
    @Getter
    private String userId;

    @Column(name = "detail", nullable = false)
    @Getter
    private String detail;

    FeedbackEntry(String uuid, LocalDateTime timestamp, String sessionId, String deployment, String namespace,
                         String userId, String detail) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
        this.deployment = deployment;
        this.namespace = namespace;
        this.userId = userId;
        this.detail = detail;
    }
}
