package org.example.notionsecurity.token

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<Token, Int> {
    @Query(
        value = """
            select * from token as t where t.user_id=:id and (t.expired = false or t.revoked = false) 
            """
    )
    fun findAllValidTokenByUser(id: Int?): List<Token>
    fun findByToken(jwt: String): Token?
}
