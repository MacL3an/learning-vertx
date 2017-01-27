/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx.test;

import java.time.Instant;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import se.maclean.learning.vertx.DataSource;
import se.maclean.learning.vertx.KryService;
import se.maclean.learning.vertx.LocalDiscDataSource;
import se.maclean.learning.vertx.ServiceProvider;

public class ServiceProviderTest {

  private DataSource mockedDataSource;

  @Before
  public void setUp() {
    mockedDataSource = mock(LocalDiscDataSource.class);
    when(mockedDataSource.readFromSource()).thenReturn(getTestData());
  }

  @Test
  public void getServicesTest() {
    ServiceProvider serviceProvider = new ServiceProvider(mockedDataSource);
    assertEquals(serviceProvider.get().size(), 2);
    KryService service = (KryService) serviceProvider.get().
            get("07a9953d-6604-4968-8bd1-df33a075980a");
    assertEquals(service.getName(), "test service");
    assertEquals(service.getUrl(), "http://kry.se");
    assertEquals(service.getStatus(), "OK");
    assertEquals(service.getLastCheck(), "2014-06-16 16:42");

  }

  @Test
  public void addServicesTest() {
    ServiceProvider serviceProvider = new ServiceProvider(mockedDataSource);
    serviceProvider.add(new KryService());
    assertEquals(serviceProvider.get().size(), 3);
    verify(mockedDataSource).persist(Mockito.anyString());
  }

  @Test
  public void removeServicesTest() {
    ServiceProvider serviceProvider = new ServiceProvider(mockedDataSource);
    serviceProvider.remove("07a9953d-6604-4968-8bd1-df33a075980a");
    assertEquals(serviceProvider.get().size(), 1);
    KryService firstService = (KryService) serviceProvider.get().values().toArray()[0];
    assertEquals(firstService.getId(), "ab38ef4d-459e-48ed-a341-c3d7936c915a");
    verify(mockedDataSource).persist(Mockito.anyString());
  }

  @Test
  public void setStatusTest() {
    ServiceProvider serviceProvider = new ServiceProvider(mockedDataSource);
    String id = "07a9953d-6604-4968-8bd1-df33a075980a";
    KryService service = (KryService) serviceProvider.get().get(id);
    assertEquals(service.getStatus(), "OK");
    serviceProvider.setStatus(id, "FAIL", Instant.now().toString());
    assertEquals(service.getStatus(), "FAIL");
    verify(mockedDataSource).persist(Mockito.anyString());
  }

  private String getTestData() {
    return "{\n"
            + "  \"services\":\n"
            + "  [\n"
            + "  {\n"
            + "  \"id\": \"07a9953d-6604-4968-8bd1-df33a075980a\",\n"
            + "          \"name\": \"test service\",\n"
            + "          \"url\": \"http://kry.se\",\n"
            + "          \"status\": \"OK\",\n"
            + "          \"lastCheck\": \"2014-06-16 16:42\"\n"
            + "  },\n"
            + "  {\n"
            + "  \"id\": \"ab38ef4d-459e-48ed-a341-c3d7936c915a\",\n"
            + "          \"name\": \"test service 2\",\n"
            + "          \"url\": \"http://google.se\",\n"
            + "          \"status\": \"OK\",\n"
            + "          \"lastCheck\": \"2014-06-17 16:42\"\n"
            + "  }  \n"
            + "  ]\n"
            + "}";
  }
}
