package pl.zbiczagromada.Magazynier.user.permissiongroups;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import java.io.ByteArrayOutputStream;
import java.util.ListIterator;
import java.util.Map;

public class PermissionGroupConverter implements AttributeConverter<Map<String, UserPermissionService.AccessLevel>, String> {

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Map<String, UserPermissionService.AccessLevel> attribute) {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(attribute);
    }

    @SneakyThrows
    @Override
    public Map<String, UserPermissionService.AccessLevel> convertToEntityAttribute(String dbData) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, UserPermissionService.AccessLevel> converted = mapper.readValue(dbData, Map.class);
        return converted;
    }
}
