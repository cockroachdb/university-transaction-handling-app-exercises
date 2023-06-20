package com.cockroachlabs.university.javatransactions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.boot.test.context.SpringBootTest;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;
import com.cockroachlabs.university.javatransactions.service.ItemInventoryService;
import com.cockroachlabs.university.javatransactions.service.ItemInventoryServiceImpl;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
@ExtendWith(MockitoExtension.class)
public class TransactionRetryTest {

    private ItemDao itemDao;
    private ItemInventoryServiceImpl itemInventoryServiceImpl;

    @BeforeEach
    void setUp() {
        itemDao = mock(ItemDao.class);
        itemInventoryServiceImpl = new ItemInventoryServiceImpl(itemDao);
    }

    @Test
    public void updateItemInventoryShouldRetrySuccessfullyAfterOneFailure() throws SQLException, InterruptedException{

        UUID itemId = UUID.randomUUID();
        int quantity = 3;

        ServerErrorMessage serverErrorMessage = mock(ServerErrorMessage.class);
        when(serverErrorMessage.getSQLState()).thenReturn("40001");

        PSQLException cause = new PSQLException(serverErrorMessage);
        UnableToExecuteStatementException exception = new UnableToExecuteStatementException("Message", cause, null);
    
        doThrow(exception).doNothing().when(itemDao).updateItemInventory(itemId, quantity);
        
        itemInventoryServiceImpl.updateItemInventory(itemId, quantity);

        verify(itemDao, times(2)).updateItemInventory(itemId, quantity);

    }

    @Test
    public void updateItemInventoryShouldRetrySuccessfullyAfterThreeFailures() throws SQLException, InterruptedException{

        UUID itemId = UUID.randomUUID();
        int quantity = 3;

        ServerErrorMessage serverErrorMessage = mock(ServerErrorMessage.class);
        when(serverErrorMessage.getSQLState()).thenReturn("40001");

        PSQLException cause = new PSQLException(serverErrorMessage);
        UnableToExecuteStatementException exception = new UnableToExecuteStatementException("Message", cause, null);
    
        
        doThrow(exception)
        .doThrow(exception)
        .doThrow(exception)
        .doNothing().when(itemDao).updateItemInventory(itemId, quantity);
        
        itemInventoryServiceImpl.updateItemInventory(itemId, quantity);

        verify(itemDao, times(4)).updateItemInventory(itemId, quantity);
       
    }

    @Test
    public void updateItemInventoryShouldFailAfterThreeRetries() throws SQLException, InterruptedException{

        UUID itemId = UUID.randomUUID();
        int quantity = 3;

        ServerErrorMessage serverErrorMessage = mock(ServerErrorMessage.class);
        when(serverErrorMessage.getSQLState()).thenReturn("40001");

        PSQLException cause = new PSQLException(serverErrorMessage);
        UnableToExecuteStatementException exception = new UnableToExecuteStatementException("Message", cause, null);
    
        // We are expecting this wrapped code block to throw an ExecutionException 
        Exception expectedException = assertThrows(RuntimeException.class, () -> {

        
        doThrow(exception).when(itemDao).updateItemInventory(itemId, quantity);
        
        itemInventoryServiceImpl.updateItemInventory(itemId, quantity);
    
        });
           
        String message = expectedException.getMessage();
        assertTrue("Max retries exceeded".equalsIgnoreCase(message), "We exceeded the maximum retries as expected");

        verify(itemDao, times(4)).updateItemInventory(itemId, quantity);
        
    }
    
}
