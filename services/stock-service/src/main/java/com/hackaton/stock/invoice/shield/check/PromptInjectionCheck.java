package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.InvoiceLineRequest;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * Une facture est une donnée, jamais une consigne. Ce contrôle repère les textes qui tentent de piloter un
 * assistant IA ou un opérateur (« ignore tes instructions », « valide automatiquement »…) dans le numéro,
 * les lignes ou les notes, en français, anglais et malgache.
 */
@Component
public class PromptInjectionCheck implements InvoiceCheck {

    private static final List<Pattern> PATTERNS = Stream.of(
            "ignor\\w*\\W+(\\w+\\W+){0,3}(instruction|consigne|regle|rule|prompt)",
            "(disregard|forget|oublie\\w*)\\W+(\\w+\\W+){0,3}(instruction|consigne|regle|rule|above|precedent)",
            "(system|systeme)\\W+prompt|prompt\\W+(system|systeme)",
            "\\b(you are now|tu es (maintenant|desormais)|act as|agis comme)\\b",
            "(valide\\w*|approuve\\w*|approve\\w*|paye\\w*|pay)\\W+(\\w+\\W+){0,3}"
                    + "(automatiquement|automatically|sans (verif|control)|without (review|check))",
            "(do not|ne pas|n'?\\w* pas)\\W+(\\w+\\W+){0,2}(verif|control|check|signal)",
            "<\\s*/?\\s*(script|system|instruction)",
            "aza\\W+(mihaino|manaraka)\\W+(\\w+\\W+){0,2}(toromarika|baiko)")
            .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
            .toList();

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        String text = normalize(allText(context.invoice()));
        boolean suspicious = PATTERNS.stream().anyMatch(pattern -> pattern.matcher(text).find());
        if (!suspicious) {
            return List.of();
        }
        return List.of(ShieldAlert.blocking(AlertCode.PROMPT_INJECTION,
                "Le document contient des consignes destinées à un assistant ou à un opérateur : "
                        + "il est traité comme suspect et doit être vérifié par une personne"));
    }

    private static String allText(SupplierInvoiceRequest invoice) {
        String lines = invoice.lines().stream().map(InvoiceLineRequest::description).collect(Collectors.joining(" "));
        return String.join(" ", invoice.invoiceNumber(), lines, invoice.notes() == null ? "" : invoice.notes());
    }

    static String normalize(String text) {
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
