package dians.finki.pipeandfilter.pipeline;

public interface Filter<I, O> {
    O process(I input);
}
