//
// Copyright (c) 2021 Copyright Holder (Catena-X Consortium)
//
// See the AUTHORS file(s) distributed with this work for additional
// information regarding authorship.
//
// See the LICENSE file(s) distributed with this work for
// additional information regarding license terms.
//
package net.catenax.irs.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import net.catenax.irs.component.enums.JobState;
import net.catenax.irs.util.JobMetrics;
import org.springframework.stereotype.Service;

/**
 * Registering customer metrics for application
 */
@Service
@Slf4j
public class MeterRegistryService {

    private final String JOB_STATE_TAG = "JOB_STATE";
    private final Counter counterCreatedJobs;

    private final JobMetrics jobMetrics;

    public MeterRegistryService(final MeterRegistry meterRegistry) {
        this.counterCreatedJobs = Counter.builder("jobs.created")
                                         .description("The number of jobs ever created")
                                         .register(meterRegistry);

        this.jobMetrics = JobMetrics.builder()
                                    .jobProcessed(Counter.builder("jobs.processed")
                                                         .description("The number jobs processed")
                                                         .tags("JOB_STATE_TAG", "processed")
                                                         .register(meterRegistry))
                                    .jobInJobStore(Counter.builder("jobs.jobstore")
                                                          .description("The number jobs in jobstore")
                                                          .tags("JOB_STATE_TAG", "jobs_in_store")
                                                          .register(meterRegistry))
                                    .jobSuccessful(Counter.builder("jobs.jobstate.sucessful")
                                                          .description("The number jobs successful")
                                                          .tags("JOB_STATE_TAG", "successful")
                                                          .register(meterRegistry))
                                    .jobFailed(Counter.builder("jobs.jobstate.failed")
                                                      .description("The number jobs failed")
                                                      .tags("JOB_STATE_TAG", "failed")
                                                      .register(meterRegistry))
                                    .jobCancelled(Counter.builder("jobs.jobstate.cancelled")
                                                         .description("The number jobs cancelled")
                                                         .tags("JOB_STATE_TAG", "cancelled")
                                                         .register(meterRegistry))
                                    .jobRunning(Counter.builder("jobs.jobstate.running")
                                                       .description("The number jobs running")
                                                       .tags("JOB_STATE_TAG", "running")
                                                       .register(meterRegistry))
                                    .exception(Counter.builder("jobs.exception")
                                                      .description("The number exceptions in jobs")
                                                      .tags("JOB_STATE_TAG", "exception")
                                                      .register(meterRegistry))
                                    .build();
    }

    /* package */ void incrementNumberOfCreatedJobs() {
        counterCreatedJobs.increment();
    }

    public void incrementJobsProcessed() {
        jobMetrics.getJobProcessed().increment();
    }

    public void incrementJobInJobStore(final Double count) {
        jobMetrics.getJobInJobStore().increment(count);
    }

    public void incrementJobSuccessful() {
        jobMetrics.getJobSuccessful().increment();
    }

    public void incrementJobFailed() {
        jobMetrics.getJobFailed().increment();
    }

    public void incrementJobCancelled() {
        jobMetrics.getJobCancelled().increment();
    }

    public void incrementJobRunning() {
        jobMetrics.getJobRunning().increment();
    }

    public void incrementException() {
        jobMetrics.getException().increment();
    }

    public void recordJobStateMetric(final JobState state) {
        switch (state) {
            case COMPLETED:
                incrementJobSuccessful();
                break;
            case TRANSFERS_FINISHED:
                incrementJobsProcessed();
                break;
            case ERROR:
                incrementJobFailed();
                break;
            case CANCELED:
                incrementJobCancelled();
                break;
            case RUNNING:
                incrementJobRunning();
                break;
            default:
        }
        log.info("Increment metric for {} state ", state);
    }

    public void setJobsInJobStore(final Integer count) {
        incrementJobInJobStore(Double.valueOf(count));
    }

    public JobMetrics getJobMetric() {
        return jobMetrics;
    }
}
