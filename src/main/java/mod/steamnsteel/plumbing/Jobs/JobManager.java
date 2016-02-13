package mod.steamnsteel.plumbing.Jobs;

import java.util.ArrayList;
import java.util.List;

public class JobManager implements IJobManager
    {
        private final List<Thread> JobThreads = new ArrayList<Thread>();
        private boolean running = true;
        private BlockingCollection<IJob> _backgroundJobs = new BlockingCollection<IJob>();
		private ConcurrentQueue<IJob> _pretickJobs = new ConcurrentQueue<IJob>();
		
	    public void AddBackgroundJob(IJob job)
	    {
		    _backgroundJobs.add(job);
	    }

	    public void AddPreTickJob(IJob job)
	    {
		    _pretickJobs.Enqueue(job);
	    }

	    public void DoPretickJobs()
	    {
		    while (!_pretickJobs.IsEmpty)
		    {
			    IJob job;
			    if (_pretickJobs.TryDequeue(out job))
			    {
				    job.execute();
			    }
		    }
	    }

        public void Start()
        {
            Stop();
            _backgroundJobs = new BlockingCollection<IJob>();
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
            _backgroundJobs.CompleteAdding();
            for (Thread thread : JobThreads)
            {
                thread.join();
            }
            JobThreads.clear();
        }

        private void StartJobThread()
        {
            while (running)
            {
                for (IJob job : _backgroundJobs.GetConsumingEnumerable())
                {
                    job.execute();
                }
            }
        }
    }
