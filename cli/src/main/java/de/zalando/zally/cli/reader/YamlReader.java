package de.zalando.zally.cli.reader;

import de.zalando.zally.cli.exception.CliException;
import de.zalando.zally.cli.exception.CliExceptionType;
import java.io.Reader;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;


public class YamlReader implements SpecsReader {

    private final Reader reader;

    public YamlReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public JSONObject read() throws CliException {
        try {
            Map<String, Object> yaml = (Map<String, Object>) new Yaml().load(reader);
            return new JSONObject(yaml);
        } catch (JSONException exception) {
            throw new CliException(CliExceptionType.CLI, "Cannot read JSON file", exception.getMessage());
        }
    }
}
