package com.jesus_crie.deusvult.utils;

public enum S {

    RESPONSE_FOOTER("Requester: %1$s"),
    RESPONSE_ERROR_COMMAND_CRASH("La commande a crashée, veuillez réessayez plus tard."),
    RESPONSE_ERROR_COMMAND_SYNTAX("Erreur de syntaxe, aucun pattern ne correspond."),
    RESPONSE_ERROR_COMMAND_NOT_FOUND("Cette commande n'éxiste pas."),
    RESPONSE_ERROR_COMMAND_GUILD_ONLY("Cette commande n'est pas disponible sur ce serveur."),
    RESPONSE_ERROR_COMMAND_WRONG_CONTEXT("Cette commande n'est pas autorisée dans ce contexte."),
    RESPONSE_ERROR_COMMAND_ACCESS_LEVEL("Vous n'avez pas les permissions requises pour cette commande."),
    RESPONSE_ERROR_COMMAND_MISSING_PERMISSION("Erreur: Il manque la permission %s");

    private String value;
    S(String value) {
        this.value = value;
    }

    public String format(Object... os) {
        return String.format(value, os);
    }

    public String get() {
        return value;
    }
}
