package com.aws.photosharing.repositry;

import com.aws.photosharing.entity.UserPhoto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserPhotoRepositryImplTest {

    @Autowired
    UserPhotoRepositry repository;

    @Test
    @Sql("/testFindByTags.sql")
    public void testFindByTags() {
        List<UserPhoto> userPhotoList = repository.findByTags("abc", new String[]{"dc", "nyc"});
        assertNotNull(userPhotoList);
        assertEquals(2, userPhotoList.size());
        boolean found123 = false;
        boolean found345 = false;
        for (UserPhoto up : userPhotoList) {
            if (up.getId().equals("123")) {
                found123 = true;
            }
            if (up.getId().equals(("345"))) {
                found345 = true;
            }
        };
        assertTrue(found123);
        assertTrue(found345);
    }
}