package org.example.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest(properties = {
        "spring.shell.interactive.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class CommentsControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticated_getAllByBook_returns401() throws Exception {
        mockMvc.perform(get("/api/books/1/comments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticated_getAllByBook_ok() throws Exception {
        mockMvc.perform(get("/api/books/1/comments").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticated_create_returns401() throws Exception {
        mockMvc.perform(post("/api/books/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticated_create_returns404() throws Exception {
        mockMvc.perform(post("/api/books/1/comments").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }
}
