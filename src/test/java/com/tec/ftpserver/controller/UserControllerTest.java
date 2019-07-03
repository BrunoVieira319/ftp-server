package com.tec.ftpserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tec.ftpserver.domain.MongoUser;
import com.tec.ftpserver.dto.UserDto;
import com.tec.ftpserver.server.MongoUserManager;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MongoUserManager userManager;

    @Test
    public void shouldReturnStatusOkWhenUserIsPosted() throws Exception {
        doNothing().when(userManager).save(any(MongoUser.class));

        UserDto user = new UserDto();
        user.setName("Name");
        user.setPassword("Psw");

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnStatusConflictWhenUserIsPosted() throws Exception {
        doThrow(FtpException.class).when(userManager).save(any(MongoUser.class));

        UserDto user = new UserDto();
        user.setName("Name");
        user.setPassword("Psw");

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isConflict());
    }
}
