package pl.zbiczagromada.Magazynier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping(path = "/server/info")
public class MagazynierApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagazynierApplication.class, args);
	}

	@GetMapping
	public ServerInfo test(){
		return new ServerInfo();
	}

	private class ServerInfo{
		public String version = "1.0";
	}

	@GetMapping("/get")
	public String xd(HttpSession session){
		return session.getId();
	}

	@PostMapping("/post")
	public String post(HttpSession session){
		return session.getId();
	}
}
