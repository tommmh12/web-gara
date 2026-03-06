package com.webgara;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;

@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
class WebGaraApplicationTests {

	@Test
	void contextLoads() {
	}

}
