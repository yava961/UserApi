package UserApi.dto.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDto(LocalDateTime localDateTime, List<String> errors) {


}
