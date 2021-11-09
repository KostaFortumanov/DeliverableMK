package dians.finki.pipeandfilter.pipeline;

public class Pipeline<I, O> {

    private final Filter<I, O> currentFilter;

    public Pipeline(Filter<I, O> currentFilter) {
        this.currentFilter = currentFilter;
    }

    public <K> Pipeline<I, K> addFilter(Filter<O, K> newFilter) {
        return new Pipeline<>(input -> newFilter.process(currentFilter.process(input)));
    }

    public O execute(I input) {
        return currentFilter.process(input);
    }
}
