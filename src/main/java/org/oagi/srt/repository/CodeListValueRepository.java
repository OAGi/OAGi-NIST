package org.oagi.srt.repository;

import org.oagi.srt.repository.entity.CodeListValue;

import java.util.List;

public interface CodeListValueRepository {

    public List<CodeListValue> findByCodeListId(int codeListId);

    public CodeListValue findOneByCodeListIdAndValue(int codeListId, String value);

    public void updateCodeListIdByCodeListValueId(int codeListId, int codeListValueId);

    public void save(CodeListValue codeListValue);
}
