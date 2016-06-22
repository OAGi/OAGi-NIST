package org.oagi.srt.validate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.persistence.populate.ImportApplication;
import org.oagi.srt.repository.XSDBuiltInTypeRepository;
import org.oagi.srt.repository.entity.XSDBuiltInType;
import org.oagi.srt.validate.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Yunsu Lee
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ImportApplication.class)
public class XSD_BuiltIn_TypeTest {

    @Autowired
    private XSDBuiltInTypeRepository xbtRepository;

    @Test
    public void testNumberOfData() {
        Assert.assertEquals(TableData.XDT_BUILT_IN_TYPE.length, xbtRepository.findAll().size());
    }

    @Test
    public void testName() {
        for (int i = 0; i < TableData.XDT_BUILT_IN_TYPE.length; i++) {
            XSDBuiltInType xVO = xbtRepository.findOneByName(TableData.XDT_BUILT_IN_TYPE[i][0]);
            if (xVO.getBuiltInType() == null)
                Assert.fail("No such type with the name, '" + TableData.XDT_BUILT_IN_TYPE[i][0] + "'r.");
        }
    }

    @Test
    public void testBuiltinType() {
        for (int i = 0; i < TableData.XDT_BUILT_IN_TYPE.length; i++) {
            XSDBuiltInType xVO = xbtRepository.findOneByName(TableData.XDT_BUILT_IN_TYPE[i][0]);
            if (xVO.getBuiltInType() == null)
                Assert.fail("No such type with the name, '" + TableData.XDT_BUILT_IN_TYPE[i][0] + "'r.");

            Assert.assertEquals(TableData.XDT_BUILT_IN_TYPE[i][1], xVO.getBuiltInType());
        }
    }

    @Test
    public void testTypeHierarchy() {
        for (int i = 0; i < TableData.XDT_BUILT_IN_TYPE.length; i++) {
            XSDBuiltInType xVO = xbtRepository.findOneByName(TableData.XDT_BUILT_IN_TYPE[i][0]);
            if (xVO.getBuiltInType() == null)
                Assert.fail("No such type with the name, '" + TableData.XDT_BUILT_IN_TYPE[i][0] + "'r.");

            XSDBuiltInType xVO1 = xbtRepository.findOne(xVO.getSubtypeOfXbtId());

            Assert.assertEquals(TableData.XDT_BUILT_IN_TYPE[i][2], xVO1.getBuiltInType());
        }
    }
}