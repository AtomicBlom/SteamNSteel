package mod.steamnsteel.plumbing.Jobs;

import jline.internal.Log;
import mod.steamnsteel.utility.log.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobManager implements IJobManager
    {
        private final List<Thread> JobThreads = new ArrayList<Thread>();
        private boolean running = true;
        private LinkedBlockingQueue<IJob> _backgroundJobs = new LinkedBlockingQueue<IJob>();
		private ConcurrentLinkedQueue<IJob> _pretickJobs = new ConcurrentLinkedQueue<IJob>();
		
	    public void AddBackgroundJob(IJob job)
	    {
		    _backgroundJobs.add(job);
	    }

	    public void AddPreTickJob(IJob job)
	    {
		    _pretickJobs.add(job);
	    }

	    public void DoPretickJobs()
	    {
            _pretickJobs.forEach(IJob::execute);
	    }

        public void Start()
        {
            Stop();
            _backgroundJobs = new LinkedBlockingQueue<IJob>();
            running = true;
	        //int processorCount = Environment.ProcessorCount;
	        int processorCount = 1;
	        for (int i = 0; i < processorCount; ++i)
            {
                Thread t = new Thread(() -> {
                    StartJobThread();
                });
				t.setName("Job Thread #" + i);
                JobThreads.add(t);
                t.start();
            }
        }

        public void Stop()
        {
            running = false;

            for (int i = 0; i < JobThreads.size(); i++)
            {
                _backgroundJobs.add(new CancelJobManagerJob());
            }

            for (final Thread thread : JobThreads)
            {
				try
				{
					thread.join();
				} catch (InterruptedException e)
				{
					Logger.warning("Unable to join Worker threads to main thread", e);
				}
			}
            JobThreads.clear();
        }

        private void StartJobThread()
        {
            while (running)
            {
                final IJob job;
                try
                {
                    job = _backgroundJobs.take();
                    if (job instanceof CancelJobManagerJob) {
                        break;
                    }

                    job.execute();
                } catch (InterruptedException e)
                {
                    Log.warn("Error executing job %s", e);
                }

            }
        }
    }
