package com.dians.deliverable.job_service.pipeline;

public interface Filter<I, O> {
    O process(I input);
}
