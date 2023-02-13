    package com.example.numblebankingserverchallenge.domain



    import com.github.f4b6a3.ulid.UlidCreator
    import jakarta.persistence.Column
    import jakarta.persistence.EntityListeners
    import jakarta.persistence.Id
    import jakarta.persistence.MappedSuperclass
    import jakarta.persistence.PostLoad
    import jakarta.persistence.PostPersist
    import org.hibernate.proxy.HibernateProxy
    import org.springframework.data.annotation.CreatedDate
    import org.springframework.data.annotation.LastModifiedDate
    import org.springframework.data.domain.Persistable
    import org.springframework.data.jpa.domain.support.AuditingEntityListener
    import java.io.Serializable
    import java.time.LocalDateTime
    import java.util.*

    @MappedSuperclass
    @EntityListeners(value = [AuditingEntityListener::class])
    abstract class PrimaryKeyEntity(
        createdDate: LocalDateTime = LocalDateTime.now(),
        updatedDate: LocalDateTime = LocalDateTime.now()
    ) : Persistable<UUID> {
        @Id
        @Column(columnDefinition = "uuid")
        private val id: UUID = UlidCreator.getMonotonicUlid().toUuid()

        @Transient
        private var _isNew = true

        @PostPersist
        @PostLoad
        protected fun load() {
            _isNew = false
        }

        @Column(updatable = false)
        @CreatedDate
        var createdDate: LocalDateTime = createdDate

        @LastModifiedDate
        var updatedDate: LocalDateTime = updatedDate

        override fun getId(): UUID = id

        override fun isNew(): Boolean = _isNew

        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (other !is HibernateProxy && this::class != other::class) return false
            return getIdentifier(other) == id
        }

        private fun getIdentifier(obj: Any): Serializable =
            if (obj is HibernateProxy) obj.hibernateLazyInitializer.identifier as Serializable else (obj as PrimaryKeyEntity).id

        override fun hashCode(): Int = Objects.hashCode(id)


    }