package io.github.mangi.eta.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface ProjectSpaceDao {
    @Query("SELECT * FROM project_spaces ORDER BY last_used_at DESC, created_at ASC")
    suspend fun spaces(): List<ProjectSpaceEntity>

    @Query("SELECT * FROM project_spaces WHERE space_key = :spaceKey LIMIT 1")
    suspend fun space(spaceKey: String): ProjectSpaceEntity?

    @Upsert
    suspend fun upsert(space: ProjectSpaceEntity)

    /** 只删除允许删除的项目；内置项目即使传入也不会被删掉。 */
    @Query("DELETE FROM project_spaces WHERE space_key = :spaceKey AND deletable = 1")
    suspend fun deleteDeletable(spaceKey: String): Int

    @Query("UPDATE project_spaces SET last_used_at = :at WHERE space_key = :spaceKey")
    suspend fun touch(spaceKey: String, at: Long)
}
