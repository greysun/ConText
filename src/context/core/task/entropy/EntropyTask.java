package context.core.task.entropy;

import context.core.entity.CTask;
import context.core.entity.CorpusData;
import context.core.entity.GenericTask;
import context.core.entity.TaskInstance;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.File;
import java.util.Properties;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Aale
 */
public class EntropyTask extends CTask{

	private static StanfordCoreNLP pipeline;
	
	static {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        pipeline = new StanfordCoreNLP(props);

    }
	
    /**
     *
     * @param progress
     * @param progressMessage
     */
    public EntropyTask(DoubleProperty progress, StringProperty progressMessage) {
        super(progress, progressMessage);
    }

    /**
     *
     * @param instance
     * @param task
     * @return
     */
    @Override
    public TaskInstance run(TaskInstance instance, GenericTask task) {
        task.progress(1, 20, "Starting Entropy Computation process...");

        EntropyTaskInstance ins = (EntropyTaskInstance) instance;
        ins.setPipeline(pipeline);
        CorpusData inputCorpus = (CorpusData) ins.getInput();
        task.progress(5, 20, "Loading " + ins.getInput().getPath().get());
        inputCorpus.addAllFiles(new File(inputCorpus.getPath().get()));

        task.progress(8, 20, inputCorpus.getFiles().size() + " files added");
        Entropybody entropyb = new Entropybody(ins);

        task.progress(10, 20, "Running Entropy Computation...");
        //Run corpus statistics
        if (!entropyb.RunEntropyComputation()) {
            System.out.println("Error in detection");
            return instance;
        }

        //Write the output to CSV
        // Need the selected output File path name !!!
        task.progress(10, 20, "Entropy Computation successfully done");
        final String path = ins.getTabularOutput(0).getPath().get();
        task.progress(14, 20, "Saving results in " + path);
        entropyb.writeOutput(path);

        task.progress(20, 20, "Results saved successfully");
        task.done();

        return ins;
    }

}
