package com.nttdata.cambioexpress;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Validador {

  private int puntaje = 0;

  @Test
  void evaluarTaller() throws IOException {
    String ci = leer(".github/workflows/ci.yml");
    String cicd = leer(".github/workflows/cicd.yml");
    String jenkins = leer("Jenkinsfile");
    String pom = leer("pom.xml");

    System.out.println("\nRN-1 Integracion continua: ci.yml");
    check("RN-1a", "Se dispara con push a feature/** y PR a master",
            contiene(ci, "name:\\s*ci pipeline") && contiene(ci, "feature/\\*\\*") && contiene(ci, "pull_request") && contiene(ci, "master"));
    check("RN-1b", "Permisos minimos contents: read y sin write",
            contiene(ci, "permissions:") && contiene(ci, "contents:\\s*read") && !contiene(ci, "write"));
    check("RN-1c", "Checkout y setup-java temurin 17 con cache maven",
            contiene(ci, "actions/checkout") && contiene(ci, "actions/setup-java") && contiene(ci, "temurin") && contiene(ci, "17") && contiene(ci, "cache:\\s*['\"]?maven"));
    check("RN-1d", "Ejecuta mvn -B -ntp verify sin saltar pruebas",
            contiene(ci, "mvn\\s+.*-B.*verify") && !contiene(ci, "skipTests|maven\\.test\\.skip|skipITs"));
    check("RN-1e", "timeout-minutes en cada job (1 a 30)",
            contiene(ci, "timeout-minutes:\\s*([1-9]|[12][0-9]|30)\\b"));

    System.out.println("\nRN-2 Testing continuo y cobertura");
    check("RN-2a", "jacoco-maven-plugin 0.8.12 con prepare-agent, report y check",
            contiene(pom, "jacoco-maven-plugin") && contiene(pom, "0.8.12") && contiene(pom, "prepare-agent") && contiene(pom, "<goal>report</goal>") && contiene(pom, "<goal>check</goal>"));
    check("RN-2b", "Regla JaCoCo LINE COVEREDRATIO >= 0.80",
            contiene(pom, "LINE") && contiene(pom, "COVEREDRATIO") && contiene(pom, "0\\.(8[0-9]|9[0-9])|1\\.00"));
    check("RN-2c", "ci.yml publica surefire-reports con if: always()",
            contiene(ci, "actions/upload-artifact") && contiene(ci, "surefire-reports") && contiene(ci, "always\\(\\)"));

    TestExecutionSummary summary = ejecutarTipoCambioTest();
    long exitosos = summary != null ? summary.getTestsSucceededCount() : 0;
    long fallidos = summary != null ? summary.getTotalFailureCount() : 1;
    check("RN-2d", "TipoCambioTest tiene al menos 4 metodos @Test", exitosos >= 4);
    check("RN-2e", "Todas las pruebas de TipoCambioTest pasan en verde", exitosos >= 4 && fallidos == 0);

    System.out.println("\nRN-3 Jenkinsfile al estilo OCD");
    check("RN-3a", "Jenkinsfile declarativo: pipeline { agent ... }",
            contiene(jenkins, "pipeline\\s*\\{") && contiene(jenkins, "agent\\s+"));
    check("RN-3b", "Etapas Compilar -> Pruebas y cobertura -> Publicar artefacto -> Desplegar a cert",
            ordenCorrecto(jenkins, "Compilar", "Pruebas y cobertura", "Publicar artefacto", "Desplegar a cert"));
    check("RN-3c", "Pruebas y cobertura ejecuta mvn verify y publica JUnit en post always",
            contiene(jenkins, "mvn\\s+.*verify") && contiene(jenkins, "post\\s*\\{[\\s\\S]*?always\\s*\\{[\\s\\S]*?junit") && contiene(jenkins, "target/\\*-reports/\\*\\.xml"));
    check("RN-3d", "options { timeout(...) } y archiveArtifacts target/cambioexpress-*.jar",
            contiene(jenkins, "options\\s*\\{[\\s\\S]*?timeout") && contiene(jenkins, "archiveArtifacts") && contiene(jenkins, "target/cambioexpress-\\*\\.jar"));
    check("RN-3e", "Desplegar solo en master y con withCredentials(cambioexpress-deploy-token)",
            contiene(jenkins, "branch\\s+['\"]master['\"]") && contiene(jenkins, "withCredentials") && contiene(jenkins, "cambioexpress-deploy-token") && contiene(jenkins, "DEPLOY_TOKEN") && contiene(jenkins, "devops/desplegar\\.sh\\s+cert\\s+target/cambioexpress-\\*\\.jar"));

    System.out.println("\nRN-4 CD al estilo Deploy Go: cicd.yml");
    check("RN-4a", "Job build: mvn verify y sube cambioexpress-jar",
            contiene(cicd, "name:\\s*cicd pipeline") && contiene(cicd, "workflow_dispatch") && contiene(cicd, "cambioexpress-jar") && contiene(cicd, "target/cambioexpress-\\*\\.jar"));
    check("RN-4b", "Job deploy-dev con needs: build y environment: dev",
            contiene(cicd, "deploy-dev:") && contiene(cicd, "needs:\\s*build") && contiene(cicd, "environment:\\s*dev"));
    check("RN-4c", "Job deploy-cert con needs: deploy-dev, environment: cert y solo master",
            contiene(cicd, "deploy-cert:") && contiene(cicd, "needs:\\s*deploy-dev") && contiene(cicd, "environment:\\s*cert") && contiene(cicd, "refs/heads/master"));
    check("RN-4d", "Despliegues descargan artefacto y ejecutan desplegar.sh con secrets.DEPLOY_TOKEN",
            contiene(cicd, "actions/download-artifact") && contiene(cicd, "devops/desplegar\\.sh\\s+dev\\s+dist/cambioexpress-\\*\\.jar") && contiene(cicd, "devops/desplegar\\.sh\\s+cert\\s+dist/cambioexpress-\\*\\.jar") && contiene(cicd, "secrets\\.DEPLOY_TOKEN"));
    check("RN-4e", "pom.xml con version 1.0.0, sin -SNAPSHOT",
            contiene(pom, "<version>1\\.0\\.0</version>") && !contiene(pom, "-SNAPSHOT"));

    System.out.println("\n==========================================");
    System.out.println("PUNTAJE: " + puntaje + " / 20");
    System.out.println("==========================================\n");

    assertTrue(puntaje > 0);
  }

  private void check(String codigo, String desc, boolean ok) {
    if (ok) {
      puntaje++;
      System.out.printf("[OK] %s %s (+1)%n", codigo, desc);
    } else {
      System.out.printf("[--] %s %s (0)%n", codigo, desc);
    }
  }

  private String leer(String ruta) throws IOException {
    Path p = Path.of(ruta);
    return Files.exists(p) ? Files.readString(p) : "";
  }

  private boolean contiene(String texto, String regex) {
    return Pattern.compile(regex, Pattern.MULTILINE).matcher(texto).find();
  }

  private boolean ordenCorrecto(String texto, String... etapas) {
    int pos = -1;
    for (String etapa : etapas) {
      int idx = texto.indexOf("stage('" + etapa + "')");
      if (idx == -1) idx = texto.indexOf("stage(\"" + etapa + "\")");
      if (idx <= pos) return false;
      pos = idx;
    }
    return true;
  }

  private TestExecutionSummary ejecutarTipoCambioTest() {
    try {
      Class<?> clazz = Class.forName("com.nttdata.cambioexpress.domain.model.TipoCambioTest");
      LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
              .selectors(DiscoverySelectors.selectClass(clazz))
              .build();
      Launcher launcher = LauncherFactory.create();
      SummaryGeneratingListener listener = new SummaryGeneratingListener();
      launcher.registerTestExecutionListeners(listener);
      launcher.execute(request);
      return listener.getSummary();
    } catch (Exception e) {
      return null;
    }
  }
}