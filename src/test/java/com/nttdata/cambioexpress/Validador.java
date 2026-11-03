package com.nttdata.cambioexpress;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validador del taller CI/CD (reconstruido a partir del enunciado).
 * Revisa ci.yml, cicd.yml, Jenkinsfile, pom.xml y ejecuta TipoCambioTest.
 * Ejecutar con: mvn -q test -Dtest=Validador
 */
class Validador {

    private int puntaje;
    private final StringBuilder salida = new StringBuilder();

    @Test
    void validar() throws Exception {
        String ci = leer(".github/workflows/ci.yml");
        String cicd = leer(".github/workflows/cicd.yml");
        String jenkins = leer("Jenkinsfile");
        String pom = leer("pom.xml");

        titulo("RN-1 Integracion continua: ci.yml");
        punto("RN-1a Se dispara con push a feature/** y PR a master",
                buscar(ci, "(?s)push:\\s*\\n\\s*branches:\\s*\\n\\s*-\\s*['\"]?feature/\\*\\*")
                        && buscar(ci, "(?s)pull_request:\\s*\\n\\s*branches:\\s*\\n\\s*-\\s*['\"]?master"));
        punto("RN-1b Permisos minimos: contents: read, sin write",
                buscar(ci, "permissions:\\s*\\n\\s*contents:\\s*read") && !ci.contains("write"));
        punto("RN-1c setup-java temurin, '17' y cache maven",
                ci.contains("actions/setup-java") && buscar(ci, "distribution:\\s*temurin")
                        && buscar(ci, "java-version:\\s*'17'") && buscar(ci, "cache:\\s*maven"));
        punto("RN-1d mvn -B -ntp verify sin saltar pruebas",
                buscar(ci, "mvn\\s+-B\\s+-ntp\\s+verify") && sinSaltos(ci));
        punto("RN-1e timeout-minutes en cada job (1 a 30)", timeoutsValidos(ci));

        titulo("RN-2 Testing continuo");
        punto("RN-2a JaCoCo 0.8.12 con prepare-agent, report y check",
                pom.contains("jacoco-maven-plugin") && pom.contains("0.8.12")
                        && pom.contains("<goal>prepare-agent</goal>") && pom.contains("<goal>report</goal>")
                        && pom.contains("<goal>check</goal>"));
        punto("RN-2b Regla LINE COVEREDRATIO con minimo >= 0.80", reglaCobertura(pom));
        punto("RN-2c upload-artifact de surefire-reports con if: always()",
                ci.contains("actions/upload-artifact") && ci.contains("target/surefire-reports/")
                        && buscar(ci, "if:\\s*always\\(\\)"));
        Class<?> clase = Class.forName("com.nttdata.cambioexpress.domain.model.TipoCambioTest");
        int[] resultado = ejecutar(clase);
        punto("RN-2d TipoCambioTest con 4 o mas @Test (" + resultado[0] + ")", resultado[0] >= 4);
        punto("RN-2e TipoCambioTest en verde (" + resultado[1] + " de " + resultado[0] + ")",
                resultado[0] >= 4 && resultado[1] == resultado[0]);

        titulo("RN-3 Jenkinsfile al estilo OCD");
        punto("RN-3a Jenkinsfile declarativo: pipeline { agent ... }",
                buscar(jenkins, "pipeline\\s*\\{\\s*(//.*\\n\\s*)*agent"));
        punto("RN-3b Etapas Compilar > Pruebas y cobertura > Publicar artefacto > Desplegar a cert",
                enOrden(jenkins, "stage('Compilar')", "stage('Pruebas y cobertura')",
                        "stage('Publicar artefacto')", "stage('Desplegar a cert')"));
        punto("RN-3c mvn verify sin saltar pruebas y junit en post always",
                buscar(jenkins, "mvn\\s+-B\\s+-ntp\\s+verify") && sinSaltos(jenkins)
                        && buscar(jenkins, "(?s)always\\s*\\{\\s*junit[^\\n]*target/\\*-reports/\\*\\.xml"));
        punto("RN-3d options { timeout(...) } y archiveArtifacts del jar",
                buscar(jenkins, "(?s)options\\s*\\{\\s*timeout\\(")
                        && jenkins.contains("target/cambioexpress-*.jar") && jenkins.contains("archiveArtifacts"));
        punto("RN-3e Desplegar solo en master y con withCredentials",
                buscar(jenkins, "(?s)when\\s*\\{\\s*branch\\s+'master'")
                        && jenkins.contains("credentialsId: 'cambioexpress-deploy-token'")
                        && jenkins.contains("variable: 'DEPLOY_TOKEN'")
                        && jenkins.contains("bash devops/desplegar.sh cert target/cambioexpress-*.jar"));

        titulo("RN-4 CD al estilo Deploy Go: cicd.yml");
        punto("RN-4a Job build: mvn verify y sube el .jar",
                cicd.contains("workflow_dispatch") && buscar(cicd, "mvn\\s+-B\\s+-ntp\\s+verify")
                        && buscar(cicd, "name:\\s*cambioexpress-jar") && cicd.contains("path: target/cambioexpress-*.jar"));
        punto("RN-4b deploy-dev con needs: build y environment: dev",
                buscar(cicd, "(?s)deploy-dev:.*?needs:\\s*build.*?environment:\\s*dev"));
        punto("RN-4c deploy-cert con needs: deploy-dev, environment: cert y solo master",
                buscar(cicd, "(?s)deploy-cert:.*?needs:\\s*deploy-dev")
                        && buscar(cicd, "(?s)deploy-cert:.*?environment:\\s*cert")
                        && buscar(cicd, "(?s)deploy-cert:.*?if:\\s*github\\.ref\\s*==\\s*'refs/heads/master'"));
        punto("RN-4d Despliegues descargan el jar, no recompilan y usan secrets.DEPLOY_TOKEN",
                despliegueSinRecompilar(cicd) && !contieneSecretoEscrito(ci) && !contieneSecretoEscrito(cicd));
        punto("RN-4e pom.xml con version X.Y.Z, sin -SNAPSHOT", versionFija(pom));

        salida.append("\nPUNTAJE: ").append(puntaje).append(" / 20\n");
        System.out.println(salida);
    }

    // ------------------------------------------------------------------ utilidades

    private static String leer(String ruta) {
        try {
            return Files.readString(Path.of(ruta), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean buscar(String texto, String regex) {
        return Pattern.compile(regex).matcher(texto).find();
    }

    private static boolean sinSaltos(String texto) {
        return !texto.contains("-DskipTests") && !texto.contains("-Dmaven.test.skip") && !texto.contains("-DskipITs");
    }

    private static boolean timeoutsValidos(String yaml) {
        int jobs = cuenta(yaml, "(?m)^  [a-z][a-z0-9-]*:\\s*$");
        Matcher m = Pattern.compile("timeout-minutes:\\s*(\\d+)").matcher(yaml);
        int validos = 0;
        while (m.find()) {
            int minutos = Integer.parseInt(m.group(1));
            if (minutos < 1 || minutos > 30) {
                return false;
            }
            validos++;
        }
        return validos > 0 && validos >= jobs;
    }

    private static int cuenta(String texto, String regex) {
        Matcher m = Pattern.compile(regex).matcher(texto.substring(Math.max(0, texto.indexOf("jobs:"))));
        int n = 0;
        while (m.find()) {
            n++;
        }
        return n;
    }

    private static boolean reglaCobertura(String pom) {
        Matcher m = Pattern.compile("(?s)<counter>LINE</counter>\\s*<value>COVEREDRATIO</value>\\s*<minimum>([0-9.]+)</minimum>")
                .matcher(pom);
        return m.find() && Double.parseDouble(m.group(1)) >= 0.80;
    }

    private static boolean enOrden(String texto, String... etapas) {
        int desde = -1;
        for (String etapa : etapas) {
            int i = texto.indexOf(etapa);
            if (i <= desde) {
                return false;
            }
            desde = i;
        }
        return true;
    }

    private static boolean despliegueSinRecompilar(String cicd) {
        for (String job : new String[]{"deploy-dev", "deploy-cert"}) {
            int inicio = cicd.indexOf("  " + job + ":");
            if (inicio < 0) {
                return false;
            }
            int fin = cicd.indexOf("\n  deploy-", inicio + 5);
            String bloque = fin < 0 ? cicd.substring(inicio) : cicd.substring(inicio, fin);
            String ambiente = job.substring("deploy-".length());
            if (!bloque.contains("actions/download-artifact") || bloque.contains("mvn ")
                    || !bloque.contains("bash devops/desplegar.sh " + ambiente + " dist/cambioexpress-*.jar")
                    || !buscar(bloque, "DEPLOY_TOKEN:\\s*\\$\\{\\{\\s*secrets\\.DEPLOY_TOKEN\\s*}}")) {
                return false;
            }
        }
        return true;
    }

    private static boolean contieneSecretoEscrito(String yaml) {
        return buscar(yaml, "(?i)(token|password|secret)\\s*:\\s*['\"]?[A-Za-z0-9_\\-]{6,}")
                && !buscar(yaml, "(?i)(token|password|secret)\\s*:\\s*\\$\\{\\{");
    }

    private static boolean versionFija(String pom) {
        String sinParent = pom.replaceAll("(?s)<parent>.*?</parent>", "");
        Matcher m = Pattern.compile("<version>([^<]+)</version>").matcher(sinParent);
        return m.find() && m.group(1).trim().matches("\\d+\\.\\d+\\.\\d+");
    }

    /** Ejecuta los metodos @Test de una clase sin extensiones; devuelve {total, en verde}. */
    private static int[] ejecutar(Class<?> clase) throws Exception {
        int total = 0;
        int verdes = 0;
        for (Method metodo : clase.getDeclaredMethods()) {
            if (!metodo.isAnnotationPresent(Test.class)) {
                continue;
            }
            total++;
            var constructor = clase.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instancia = constructor.newInstance();
            metodo.setAccessible(true);
            try {
                metodo.invoke(instancia);
                verdes++;
            } catch (InvocationTargetException e) {
                System.out.println("  [falla] " + metodo.getName() + ": " + e.getCause());
            }
        }
        return new int[]{total, verdes};
    }

    private void titulo(String texto) {
        salida.append('\n').append(texto).append('\n');
    }

    private void punto(String descripcion, boolean ok) {
        if (ok) {
            puntaje++;
        }
        salida.append(ok ? "  [OK] " : "  [--] ").append(descripcion).append(ok ? "  +1" : "  0").append('\n');
    }
}
