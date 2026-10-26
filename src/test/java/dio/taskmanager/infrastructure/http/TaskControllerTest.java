package dio.taskmanager.infrastructure.http;

import dio.taskmanager.domain.Task;
import dio.taskmanager.domain.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class TaskControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentationContextProvider
    ) {
        repository.findAll().forEach(task -> repository.delete(task.getId()));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.findAll().forEach(task -> repository.delete(task.getId()));
    }

    @Test
    void shouldCreateTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Estudar Spring REST Docs",
                                  "description": "Documentar a API a partir dos testes"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Estudar Spring REST Docs"))
                .andExpect(jsonPath("$.description").value("Documentar a API a partir dos testes"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andDo(documentEndpoint(
                        "tasks-create",
                        requestFields(
                                fieldWithPath("title").description("Título da tarefa"),
                                fieldWithPath("description").description("Descrição da tarefa").optional()
                        ),
                        responseFields(taskResponseFields())
                ));
    }

    @Test
    void shouldListTasks() throws Exception {
        Task task = saveTask("Tarefa listada", "Descrição da tarefa listada");

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(task.getId().id().toString()))
                .andExpect(jsonPath("$[0].title").value("Tarefa listada"))
                .andExpect(jsonPath("$[0].description").value("Descrição da tarefa listada"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andDo(documentEndpoint(
                        "tasks-list",
                        responseFields(taskListResponseFields())
                ));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        Task task = saveTask("Tarefa encontrada", "Descrição da tarefa encontrada");

        mockMvc.perform(get("/tasks/{id}", task.getId().id()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId().id().toString()))
                .andExpect(jsonPath("$.title").value("Tarefa encontrada"))
                .andExpect(jsonPath("$.description").value("Descrição da tarefa encontrada"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andDo(documentEndpoint(
                        "tasks-get-by-id",
                        pathParameters(parameterWithName("id").description("Identificador UUID da tarefa")),
                        responseFields(taskResponseFields())
                ));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task task = saveTask("Título original", "Descrição original");

        mockMvc.perform(patch("/tasks/{id}", task.getId().id())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Título atualizado",
                                  "description": "Descrição atualizada",
                                  "status": "IN_PROGRESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId().id().toString()))
                .andExpect(jsonPath("$.title").value("Título atualizado"))
                .andExpect(jsonPath("$.description").value("Descrição atualizada"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andDo(documentEndpoint(
                        "tasks-update",
                        pathParameters(parameterWithName("id").description("Identificador UUID da tarefa")),
                        requestFields(
                                fieldWithPath("title").description("Novo título da tarefa").optional(),
                                fieldWithPath("description").description("Nova descrição da tarefa").optional(),
                                fieldWithPath("status").description("Novo status: PENDING, IN_PROGRESS ou COMPLETED").optional()
                        ),
                        responseFields(taskResponseFields())
                ));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task task = saveTask("Tarefa a excluir", "Descrição da tarefa a excluir");

        mockMvc.perform(delete("/tasks/{id}", task.getId().id()))
                .andExpect(status().isNoContent())
                .andDo(documentEndpoint(
                        "tasks-delete",
                        pathParameters(parameterWithName("id").description("Identificador UUID da tarefa"))
                ));

        assertTrue(repository.findById(task.getId()).isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        UUID missingId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(get("/tasks/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")))
                .andDo(documentEndpoint(
                        "tasks-get-not-found",
                        pathParameters(parameterWithName("id").description("Identificador UUID da tarefa inexistente"))
                ));
    }

    @Test
    void shouldRejectInvalidTaskCreation() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Descrição válida"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andDo(documentEndpoint(
                        "tasks-create-validation-error",
                        requestFields(
                                fieldWithPath("title").description("Título inválido da tarefa"),
                                fieldWithPath("description").description("Descrição da tarefa").optional()
                        ),
                        responseFields(fieldWithPath("title").description("Mensagem de validação do título"))
                ));
    }

    @Test
    void shouldRejectBlankTitleWhenUpdatingTask() throws Exception {
        Task task = saveTask("Título original", "Descrição original");

        mockMvc.perform(patch("/tasks/{id}", task.getId().id())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andDo(documentEndpoint(
                        "tasks-update-validation-error",
                        pathParameters(parameterWithName("id").description("Identificador UUID da tarefa")),
                        requestFields(fieldWithPath("title").description("Título inválido da tarefa")),
                        responseFields(fieldWithPath("title").description("Mensagem de validação do título"))
                ));
    }

    private Task saveTask(String title, String description) {
        return repository.save(new Task(title, Optional.of(description)));
    }

    private static RestDocumentationResultHandler documentEndpoint(String identifier, Snippet... snippets) {
        return document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                snippets
        );
    }

    private static FieldDescriptor[] taskResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("Identificador UUID da tarefa"),
                fieldWithPath("title").description("Título da tarefa"),
                fieldWithPath("description").description("Descrição da tarefa").optional(),
                fieldWithPath("status").description("Status atual da tarefa")
        };
    }

    private static FieldDescriptor[] taskListResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("[].id").description("Identificador UUID da tarefa"),
                fieldWithPath("[].title").description("Título da tarefa"),
                fieldWithPath("[].description").description("Descrição da tarefa").optional(),
                fieldWithPath("[].status").description("Status atual da tarefa")
        };
    }
}
