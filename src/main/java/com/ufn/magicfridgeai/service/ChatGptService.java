package com.ufn.magicfridgeai.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ufn.magicfridgeai.model.FoodItem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ChatGptService {

    private final WebClient webClient;
    private String apiKey = System.getenv("API_KEY");

    public ChatGptService(WebClient webClient) {
        this.webClient = webClient;
    }

/*
* curl https://api.openai.com/v1/chat/completions \ URL da API
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \  Chave da API
  -d '{
    "model": "gpt-4o",   Aqui é o modelo disponivel da IA
    "store": true,       Salva a resposta da IA no banco de dados
    "messages": [
      {"role": "user", "content": "write a haiku about ai"}   A mensagem que vai ser enviada para a IA
    ]
  }'
* */

    public Mono<String> generateRecipe(List<FoodItem> foodItems) {

        String alimentos = foodItems.stream()
                .map(item -> String.format("%s (%s) - Quantidade: %d, Validade: %s",
                        item.getNome(), item.getCategoria(), item.getQuantidade(), item.getValidade()))
                .collect(Collectors.joining("\n"));

        String prompt = "Baseado no meu banco de dados faça uma receita com os seguintes itens: \n " + alimentos;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", """
                                Você é um chef de cozinha experiente. Sua tarefa é criar receitas práticas e saborosas
                                utilizando APENAS (ou prioritariamente) a lista de ingredientes fornecida.
                                
                                Responda sempre com a seguinte estrutura:
                                - Nome da Receita
                                - Tempo Estimado de Preparo
                                - Ingredientes Utilizados
                                - Passo a Passo
                                """),
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Manda dados no formato JSON
                .header("Authorization", "Bearer " + apiKey) // Anexa a chave da API no header
                .bodyValue(requestBody)
                .retrieve() // Abre a conexão e envia o pacote HTTP via internet para a OpenAI
                .bodyToMono(Map.class) // Converte o JSON recebido de volta para um objeto Map
                .map(response -> { //Aqui ele filtra o JSON para pegar apenas o que eu quero (a receita)
                    var choices = (List<Map<String, Object>>) response.get("choices"); // Busca a lista de respostas geradas pela API
                    if (choices != null && !choices.isEmpty()) { // Valida se a lista veio com alguma resposta válida
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message"); // Pega o objeto da mensagem na primeira opção
                        return message.get("content").toString(); // Extrai e retorna o texto da receita
                    }
                    return "Nenhuma receita foi gerada.";
                });
    }
}