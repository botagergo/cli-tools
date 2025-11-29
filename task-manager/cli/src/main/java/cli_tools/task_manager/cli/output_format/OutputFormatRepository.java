package cli_tools.task_manager.cli.output_format;

public interface OutputFormatRepository {

    OutputFormat get(String name);
    void create(String name, OutputFormat outputFormat);

}
