package com.mySpringProject.main.web;

import com.mySpringProject.main.domain.posts.Posts;
import com.mySpringProject.main.domain.posts.PostsRepository;
import com.mySpringProject.main.web.dto.PostsSaveRequestDto;
import com.mySpringProject.main.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_is_created() throws Exception {
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("kjh")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void Posts_id_updated() throws Exception {
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("before")
                .content("before content")
                .author("before author")
                .build());

        String exchangedTitle = "after";
        String exchangedContent = "after content";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(exchangedTitle)
                .content(exchangedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + savedPosts.getId();

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(exchangedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(exchangedContent);
    }

    @Test
    public void Post_is_deleted() {
        Posts savedPosts = postsRepository.save(Posts.builder()
                .title("delete")
                .content("delete")
                .author("delete")
                .build());

        String url = "http://localhost:" + port + "/api/v1/posts/" + savedPosts.getId();

        restTemplate.delete(url);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }
}
