package com.eventticketing.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970",
		"jwt.expiration=86400000"
})
@ActiveProfiles("test")
class EventTicketingUserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
