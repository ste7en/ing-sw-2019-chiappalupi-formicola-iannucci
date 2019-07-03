package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.utility.Loggable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Helper interface for RMI class handlers used to
 * send asynchronous tasks in client/server communications.
 *
 * @author Stefano Formicola
 */
@SuppressWarnings("squid:S1214")
public interface RMIAsyncHelper extends Loggable {
    /**
     * Log strings
     */
    String RMI_EXCEPTION = "Remote RMI exception: ";
    String INTERRUPTED_EXCEPTION = "Interrupted exception: ";

    /**
     * @param executorService an executor responsible to manage async tasks
     * @param task a callable task executed in parallel
     */
    default void submitRemoteMethodInvocation(ExecutorService executorService, Callable<Void> task) {
        executorService.submit( () -> {
           try {
               task.call();
           } catch (ExecutionException e) {
               logOnException(RMI_EXCEPTION + e.getCause(), e);
           } catch (InterruptedException e) {
               logOnException(INTERRUPTED_EXCEPTION +e.getCause(), e);
               Thread.currentThread().interrupt();
           } catch (Exception e) {
               logOnException(e.getMessage(), e);
           }
        });
    }
}