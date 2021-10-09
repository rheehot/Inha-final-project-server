package com.dragonappear.inha.service.user;

import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class UserServiceTest {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User newUser1 = new User("name1", "nickname1", "email1@", "userTel11");
        User newUser2 = new User("name2", "nickname2", "email2@", "userTel22");
        User newUser3 = new User("name3", "nickname3", "email3@", "userTel33");
        User newUser4 = new User("name4", "nickname4", "email4@", "userTel44");
        userRepository.save(newUser1);
        userRepository.save(newUser2);
        userRepository.save(newUser3);
        userRepository.save(newUser4);
    }

    @Test
    public void 유저등록_테스트() throws Exception{
        //given
        User newUser = new User("name1", "nickname1", "email1", "userTel1");
        userService.join(newUser);
        //when
        User findUser = userRepository.findById(newUser.getId()).get();
        //then
        assertThat(findUser).isEqualTo(newUser);
        assertThat(findUser.getId()).isEqualTo(newUser.getId());
        assertThat(findUser.getNickname()).isEqualTo(newUser.getNickname());
        assertThat(findUser.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(findUser.getUserTel()).isEqualTo(newUser.getUserTel());
    }

    @Test
    public void 유저중복등록_테스트() throws Exception{
        //given
        User newUser = new User("name1", "nickname1", "email1", "userTel1");
        userRepository.save(newUser);
        //when
        User errorUser = new User("name1", "nickname1", "email1", "userTel1");
        //then
        assertThrows(IllegalStateException.class, ()-> {userService.join(errorUser);});
    }

    @Test
    public void 유저단건조회_테스트() throws Exception{
        //given
        User user = userRepository.findByEmail("email1@").get();
        //when
        User findUser = userService.findOne(user.getId());
        //then
        assertThat(findUser).isEqualTo(user);
        assertThat(findUser.getId()).isEqualTo(user.getId());
        assertThat(findUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(findUser.getUserTel()).isEqualTo(user.getUserTel());
    }

    @Test
    public void 모든유저조회_테스트() throws Exception{
        //given
        //when
        List<User> all = userService.findAll();
        //then
        assertThat(all.size()).isEqualTo(4);
        assertThat(all).extracting("email").containsOnly("email1@", "email2@", "email3@", "email4@");
        assertThat(all).extracting("nickname").containsOnly("nickname1", "nickname2", "nickname3", "nickname4");
        assertThat(all).extracting("userTel").containsOnly("userTel11","userTel22","userTel33","userTel44");
        assertThat(all).extracting("username").containsOnly("name1", "name2", "name3", "name4");
    }

}