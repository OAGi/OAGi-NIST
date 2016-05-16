package org.oagi.srt.repository;

import org.oagi.srt.repository.entity.BasicBusinessInformationEntitySupplementaryComponent;

import java.util.List;

public interface BasicBusinessInformationEntitySupplementaryComponentRepository {

    public int findGreatestId();

    public List<BasicBusinessInformationEntitySupplementaryComponent> findByBbieId(int bbieId);

    public void save(BasicBusinessInformationEntitySupplementaryComponent basicBusinessInformationEntitySupplementaryComponent);

    public void update(BasicBusinessInformationEntitySupplementaryComponent basicBusinessInformationEntitySupplementaryComponent);
}