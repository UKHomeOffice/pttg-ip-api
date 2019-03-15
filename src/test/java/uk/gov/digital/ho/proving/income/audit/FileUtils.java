package uk.gov.digital.ho.proving.income.audit;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class FileUtils {

    static String loadJsonResource(Resource resource) {

        try {
            return new String(Files.readAllBytes(Paths.get(resource.getURI())));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read from resource " + resource);
        }
    }
}
