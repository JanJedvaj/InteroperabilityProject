package hr.algebra.books;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.books.user.domain.AppUser;
import hr.algebra.books.user.domain.Role;
import hr.algebra.books.user.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Happy-path smoke tests for the Books REST API.
 * Uses H2 in-memory DB (application-test.yml) — no SQL Server required.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerSmokeTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired AppUserRepository userRepository;
    @Autowired BCryptPasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeAll
    void seedUsers() {
        if (userRepository.count() == 0) {
            userRepository.save(new AppUser("reader", passwordEncoder.encode("reader123"), Role.READ));
            userRepository.save(new AppUser("admin",  passwordEncoder.encode("admin123"),  Role.FULL));
        }
    }

    @BeforeEach
    void obtainAdminToken() throws Exception {
        String body = """
                {"username":"admin","password":"admin123"}
                """;
        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        adminToken = json.get("accessToken").asText();
        assertThat(adminToken).isNotBlank();
    }

    @Test
    void listBooks_withAuth_returns200() throws Exception {
        mvc.perform(get("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void listBooks_withoutAuth_returns401() throws Exception {
        mvc.perform(get("/api/v1/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBook_knownId_returns200() throws Exception {
        // Create one book first so we have a guaranteed record
        String newBook = """
                {"title":"GetTest Book","pageCount":50}
                """;
        MvcResult createResult = mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isCreated())
                .andReturn();
        long id = mapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(get("/api/v1/books/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void createBook_withAdminToken_returns201() throws Exception {
        String newBook = """
                {
                  "title": "Test Book",
                  "description": "Created by smoke test",
                  "pageCount": 100,
                  "excerpt": "Excerpt text",
                  "publishDate": "2024-01-15T00:00:00Z"
                }
                """;

        MvcResult result = mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = mapper.readTree(result.getResponse().getContentAsString());
        assertThat(created.get("id").asLong()).isPositive();
        assertThat(created.get("title").asText()).isEqualTo("Test Book");
    }

    @Test
    void createBook_withReaderToken_returns403() throws Exception {
        String readerLogin = """
                {"username":"reader","password":"reader123"}
                """;
        MvcResult loginResult = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readerLogin))
                .andExpect(status().isOk())
                .andReturn();
        String readerToken = mapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken").asText();

        String newBook = """
                {"title":"Forbidden","pageCount":1}
                """;
        mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + readerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBook_withoutToken_returns401() throws Exception {
        String newBook = """
                {"title":"Unauthorized","pageCount":1}
                """;
        mvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateBook_withAdminToken_returns200() throws Exception {
        String newBook = """
                {"title":"Before Update","pageCount":50}
                """;
        MvcResult createResult = mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isCreated())
                .andReturn();
        long id = mapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        String updated = """
                {"title":"After Update","pageCount":99}
                """;
        mvc.perform(put("/api/v1/books/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("After Update"));
    }

    @Test
    void deleteBook_withAdminToken_returns204() throws Exception {
        String newBook = """
                {"title":"To Delete","pageCount":1}
                """;
        MvcResult createResult = mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isCreated())
                .andReturn();
        long id = mapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(delete("/api/v1/books/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/books/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBooks_byTerm_returnsFilteredResults() throws Exception {
        String newBook = """
                {"title":"UniqueSearchableTitleXYZ","pageCount":10}
                """;
        mvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBook))
                .andExpect(status().isCreated());

        MvcResult result = mvc.perform(get("/api/v1/books?term=uniquesearchable")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode books = mapper.readTree(result.getResponse().getContentAsString());
        assertThat(books.isArray()).isTrue();
        assertThat(books.size()).isGreaterThanOrEqualTo(1);
        assertThat(books.get(0).get("title").asText()).contains("UniqueSearchable");
    }
}
