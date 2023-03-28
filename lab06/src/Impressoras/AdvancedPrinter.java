package src.Impressoras;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AdvancedPrinter implements AdvancedPrinterInterface {

    // https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html
    class PrinterService implements Runnable {
        private final LinkedBlockingQueue<PrintJob> printQueue;
        private final ExecutorService pool;
     
        // este serviço simula a fila de impressão e a impressão de um documento de cada vez
        public PrinterService() {
                printQueue = new LinkedBlockingQueue<>();
                pool = Executors.newFixedThreadPool(1);
        }
     
        public void run() { // run the service
            try {
                for (;;) {
                    PrintJob j = printQueue.take();
                    pool.submit(j).get();
                }
            } catch (java.util.concurrent.RejectedExecutionException ex) {
                System.out.println("Job rejected by spool: service shutting down?");
            } catch (ExecutionException e) {
                System.out.println("Error");
                e.printStackTrace();
            } catch (InterruptedException ex) {
            this.shutdownAndAwaitTermination();
            }
        }

        public int newPrintJob(Document doc) {
           // TODO: adiciona 'print job' à fila de impressão
            PrintJob job = new PrintJob(doc);
            printQueue.add(job);
            return printQueue.size() - 1;
        }

        public boolean cancelJob(int job) {
           // TODO: cancela 'print job', se existir na fila
           if (job < 0 || job >= printQueue.size()) {
               return false;
           }

           else {
               printQueue.remove(job);
               return true;
           }

        }
    
        void shutdownAndAwaitTermination() {
            pool.shutdown(); // Disable new tasks from being submitted
            try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Spool did not terminate.");
            }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                printQueue.clear();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }

        public LinkedBlockingQueue<PrintJob> getPrintQueue() {
            return printQueue;
        }

    }

    private PrinterService spool;

    public AdvancedPrinter() {
        this.spool = new PrinterService();
        new Thread(this.spool).start();
    }

    // TODO: implementar métodos
    @Override
    public int print(Document doc) {
        return spool.newPrintJob(doc);
    }

    @Override
    public List<Integer> print(List<Document> docs) {
        List<Integer> jobIds = new ArrayList<>();
        for (Document doc : docs) {
            int jobId = print(doc);
            if (jobId == -1) {
                return null;
            }
            jobIds.add(jobId);
        }
        return jobIds;
    }

    @Override
    public void showQueuedJobs() {
        for (int i = 0; i < spool.getPrintQueue().size(); i++) {
            System.out.println("* Job " + i + ": " + spool.getPrintQueue().get(i).getDoc().getContent());
        }
    }

    @Override
    public boolean cancelJob(int jobId) {
        return spool.cancelJob(jobId);
    }

    @Override
    public void cancelAll() {
        spool.getPrintQueue().clear();
    }

    

}
