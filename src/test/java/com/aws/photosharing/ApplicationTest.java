package com.aws.photosharing;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTest {

    @Autowired
    AmazonS3 amazonS3;

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        @Primary
        public AmazonS3 amazonS3() throws Exception {
            S3Mock api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
            api.start();
            EndpointConfiguration endpoint = new EndpointConfiguration("http://localhost:8001", "us-west-2");
            AmazonS3 amazonS3 = AmazonS3ClientBuilder
                    .standard()
                    .withPathStyleAccessEnabled(true)
                    .withEndpointConfiguration(endpoint)
                    .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                    .build();

            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            amazonS3.createBucket("admin");
            amazonS3.putObject("admin", "privateKey", new ByteArrayInputStream(keyPair.getPrivate().getEncoded()), null);
            amazonS3.putObject("admin", "publicKey", new ByteArrayInputStream(keyPair.getPublic().getEncoded()), null);

            return amazonS3;
        }
    }

    @Test
    public void test() throws Exception {
        post("/v1/photo").then().statusCode(403);
        post("/v1/authenticate?userName=test&password=test").then().statusCode(200);

        Response response = post("/v1/authenticate?userName=test&password=test");
        String jwt = response.getHeader(AUTHORIZATION);
        assertNotNull(jwt);
        response.then().statusCode(200);

        File file = File.createTempFile("test", "file");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("test\n");
        bw.close();

        assertFalse(amazonS3.doesBucketExistV2("test"));
        response = given().multiPart("file", file).header(AUTHORIZATION, jwt).post("/v1/photo");
        String location = response.getHeader("Location");
        response.then().statusCode(201);
        assertNotNull(location);
        assertNotEquals("", location);

        assertTrue(amazonS3.doesBucketExistV2("test"));

        String id1 = location.substring(location.lastIndexOf("/") + 1);

        response = given().multiPart("file", file).header(AUTHORIZATION, jwt).post("/v1/photo");
        location = response.getHeader("Location");
        response.then().statusCode(201);
        assertNotNull(location);
        assertNotEquals("", location);

        String id2 = location.substring(location.lastIndexOf("/") + 1);

        assertNotEquals(id1, id2);

        response = given().header(AUTHORIZATION, jwt).get("/v1/photo?tags=nc");
        String json = response.getBody().asString();
        response.then().statusCode(200);
        assertTrue(json.isEmpty());

        given().header(AUTHORIZATION, jwt)
                .param("id", id1)
                .param("tags", "dc,nyc")
                .put("/v1/photo")
                .then()
                .statusCode(200);

        given().header(AUTHORIZATION, jwt)
                .param("id", id2)
                .param("tags", "dc")
                .put("/v1/photo")
                .then()
                .statusCode(200);

        given().header(AUTHORIZATION, jwt)
                .param("id", "IDNOTFOUND")
                .param("tags", "NC")
                .put("/v1/photo")
                .then()
                .statusCode(404);

        response = given().header(AUTHORIZATION, jwt).get("/v1/photo?tags=nc");
        json = response.getBody().asString();
        response.then().statusCode(200);
        assertTrue(json.isEmpty());

        response = given().header(AUTHORIZATION, jwt).get("/v1/photo?tags=dc");
        List<String> urls = response.getBody().jsonPath().get("urls");
        assertEquals(2, urls.size());
        response.then().statusCode(200);

        response = given().header(AUTHORIZATION, jwt).get("/v1/photo?tags=nyc");
        urls = response.getBody().jsonPath().get("urls");
        assertEquals(1, urls.size());
        response.then().statusCode(200);
    }
}