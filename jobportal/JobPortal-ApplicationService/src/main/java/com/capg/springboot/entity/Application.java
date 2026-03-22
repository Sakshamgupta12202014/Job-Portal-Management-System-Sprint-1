package com.capg.springboot.entity;

import com.capg.springboot.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "applications",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "job_id"}, // one application per job per user — enforced at DB level
        name = "uk_user_job"
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // job seeker who applied

    @Column(name = "job_id", nullable = false)
    private Long jobId; // job applied to

    @Column(name = "resume_url", nullable = false, columnDefinition = "TEXT")
    private String resumeUrl; // resume submitted with this application

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter; // optional

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "recruiter_note", columnDefinition = "TEXT")
    private String recruiterNote; // internal note — NOT visible to the seeker

    @CreationTimestamp
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // status change timestamp
}
