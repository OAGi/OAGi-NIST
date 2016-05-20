package org.oagi.srt.repository.oracle;

import org.oagi.srt.repository.entity.BlobContent;
import org.oagi.srt.repository.impl.BaseBlobContentRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OracleBlobContentRepository extends BaseBlobContentRepository {

    private final String SAVE_STATEMENT = "INSERT INTO blob_content (" +
            "blob_content_id, content, release_id, module) VALUES (" +
            "blob_content_id_seq.NEXTVAL, :content, :release_id, :module)";

    @Override
    protected int doSave(MapSqlParameterSource namedParameters,
                         BlobContent blobContent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(SAVE_STATEMENT, namedParameters, keyHolder, new String[]{"blob_content_id"});
        return keyHolder.getKey().intValue();
    }
}
