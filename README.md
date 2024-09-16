import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebHookFailedTransactionProcessorTest {

    @Mock
    private MemberConfigDao memberConfigDao;

    @Mock
    private WebHookResponseDao webHookResponseDao;

    @Mock
    private ConnectorServiceUtils connectorServiceUtils;

    @Mock
    private Environment env;

    @Mock
    private RequestsAuditDao requestsAuditDao;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OkHttpClient okHttpClient;

    @InjectMocks
    private WebHookFailedTransactionProcessor webHookFailedTransactionProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessFailedWebHookRecords_Success() {
        // Arrange
        MemberConfig memberConfig = new MemberConfig();
        List<MemberConfig> memberConfigs = Collections.singletonList(memberConfig);
        when(memberConfigDao.getWebHookEnabledConfigDetails()).thenReturn(memberConfigs);

        // Act
        webHookFailedTransactionProcessor.processFailedWebHookRecords();

        // Assert
        verify(memberConfigDao, times(1)).getWebHookEnabledConfigDetails();
        verify(webHookResponseDao, times(1)).getWebHookFailedTransactions(anyString(), anyString());
    }

    @Test
    void testProcessFailedWebHookRecords_NoRecords() {
        // Arrange
        when(memberConfigDao.getWebHookEnabledConfigDetails()).thenReturn(Collections.emptyList());

        // Act
        webHookFailedTransactionProcessor.processFailedWebHookRecords();

        // Assert
        verify(memberConfigDao, times(1)).getWebHookEnabledConfigDetails();
        verify(webHookResponseDao, never()).getWebHookFailedTransactions(anyString(), anyString());
    }

    @Test
    void testProcessFailedTransaction_Success() throws Exception {
        // Arrange
        MemberConfig memberConfig = mock(MemberConfig.class);
        WebHookFailedTransactions failedTransaction = mock(WebHookFailedTransactions.class);
        WebHookRequest webHookRequest = mock(WebHookRequest.class);
        WebHookRequestEntry webHookRequestEntry = mock(WebHookRequestEntry.class);

        when(objectMapper.readValue(anyString(), eq(WebHookRequest.class))).thenReturn(webHookRequest);
        when(webHookRequest.getEntries()).thenReturn(Collections.singletonList(webHookRequestEntry));
        when(webHookRequestEntry.getDecisionLabels()).thenReturn(Collections.singletonList("APPROVE"));
        when(failedTransaction.getRetry()).thenReturn(1);
        when(memberConfig.getWebHookRetry()).thenReturn("3");

        // Act
        webHookFailedTransactionProcessor.processFailedTransaction(memberConfig, failedTransaction, Collections.emptySet(), 1);

        // Assert
        verify(webHookResponseDao, times(1)).updateWebHookFailedTransactionsRetry(anyString(), anyString(), anyString(), anyInt(), eq("SUCCESS"));
        verify(connectorServiceUtils, times(1)).sendDataToQueue(any(), any(), anyString(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testProcessFailedTransaction_RetryLimitReached() {
        // Arrange
        MemberConfig memberConfig = mock(MemberConfig.class);
        WebHookFailedTransactions failedTransaction = mock(WebHookFailedTransactions.class);

        when(failedTransaction.getRetry()).thenReturn(3);
        when(memberConfig.getWebHookRetry()).thenReturn("3");

        // Act
        webHookFailedTransactionProcessor.processFailedTransaction(memberConfig, failedTransaction, Collections.emptySet(), 3);

        // Assert
        verify(webHookResponseDao, never()).updateWebHookFailedTransactionsRetry(anyString(), anyString(), anyString(), anyInt(), eq("SUCCESS"));
        verify(connectorServiceUtils, never()).sendDataToQueue(any(), any(), anyString(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testProcessWithSAR_ReturnsResponse() {
        // Arrange
        MemberConfig memberConfig = mock(MemberConfig.class);
        WebHookFailedTransactions failedTransaction = mock(WebHookFailedTransactions.class);
        WebHookRequestEntry webHookRequestEntry = mock(WebHookRequestEntry.class);

        // Act
        Optional<String> response = webHookFailedTransactionProcessor.processWithSAR(memberConfig, failedTransaction, false, webHookRequestEntry);

        // Assert
        assertTrue(response.isPresent());
        assertEquals("Processed successfully", response.get());
    }

    @Test
    void testProcessWithSAR_ReturnsEmptyOnError() {
        // Arrange
        MemberConfig memberConfig = mock(MemberConfig.class);
        WebHookFailedTransactions failedTransaction = mock(WebHookFailedTransactions.class);
        WebHookRequestEntry webHookRequestEntry = mock(WebHookRequestEntry.class);

        // Simulate error case if needed by adjusting the mock behavior here.

        // Act
        Optional<String> response = webHookFailedTransactionProcessor.processWithSAR(memberConfig, failedTransaction, true, webHookRequestEntry);

        // Assert
        assertFalse(response.isPresent());
    }
}
