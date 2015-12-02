package com.yidumen.datatransform;

import com.yidumen.datatransform.dao.ACERepository;
import com.yidumen.datatransform.dao.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author 蔡迪旻
 *         2015年11月30日
 */
@Component
public class Transform implements CommandLineRunner {
    @Autowired
    private ACERepository aceDao;
    @Autowired
    private LocalRepository localDao;

    @Override
    public void run(String... strings) throws Exception {
        localDao.transTag();
        localDao.transRecording();
        localDao.transSutra();
        localDao.transOther();
        localDao.transWechat();
//        localDao.insertVideo();
    }
}
