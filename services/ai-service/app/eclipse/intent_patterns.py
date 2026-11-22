from __future__ import annotations

import re

from app.eclipse.text import any_of

ADD_VERBS = any_of(r"ajout\w*", r"rajout\w*", r"activ(?:e|er|ez)\b", r"mets?\b", r"mettre\b", r"integr\w*",
                   r"(?:fais|faire|faites) apparaitre\b")
WISH = any_of(r"je (?:veux|voudrais|souhaite)\b", r"j aimerais\b", r"on (?:veut|voudrait)\b",
              r"il (?:me |nous )?faut\b", r"suivre\b", r"gerer\b", r"noter\b", r"previen\w*", r"avertis\w*")
REMOVAL_VERBS = any_of(r"supprim\w*", r"enlev\w*", r"retir\w*", r"cach(?:e|er|ez)\b", r"masqu\w*",
                       r"desactiv\w*", r"effac\w*")
QUESTION_WORDS = any_of(
    r"combien\b", r"quel(?:le)?s?\b", r"pourquoi\b", r"comment\b", r"liste[rz]?\b", r"voir\b",
    r"montre[rz]?\b", r"affiche[rz]?\b", r"donne[rz]?\b", r"cherche[rz]?\b", r"trouve[rz]?\b",
    r"est ce qu", r"y a t il\b", r"qu est ce\b", r"savoir\b", r"connaitre\b", r"\w+ t (?:il|elle|on)\b",
    r"(?:ai|as|est|sont|peut|peux|puis|dois|doit|faut|avons|avez|ont|va|vont) "
    r"(?:je|tu|il|elle|on|nous|vous|ils|elles)\b")
QUESTION_START = re.compile(r"^(?:(?:a|de|pour|avec|depuis) )?(?:qui|quand|ou)\b")
PERIOD_WORDS = any_of(r"aujourd hui\b", r"hier\b", r"demain\b", r"(?:cette|la) semaine\b", r"ce mois\b",
                      r"cette annee\b", r"ce (?:matin|soir)\b", r"en retard\b")
_POLITE = r"^(?:(?:peux tu|pouvez vous|merci de|stp|svp|s il te plait|s il vous plait) )?"
_RECORDS = r"(?:ventes?|factures?|commandes?|clients?|clientes?|produits?|employes?|fournisseurs?|conges?|mouvements?)"
BUSINESS_ACTION = re.compile(
    _POLITE + r"(?:(?:enregistre[rz]?|vends|vendez|encaisse[rz]?|facture[rz]?|rembourse[rz]?|saisi[rs]?)\b"
    r"|(?:fais|faire|faites|cree[rz]?|passe[rz]?|ajoute[rz]?) (?:moi )?(?:un|une|\d+) " + _RECORDS + r"\b"
    r"|(?:nouveau|nouvel|nouvelle) " + _RECORDS + r"\b)")
EXPLICIT_CREATE = any_of(
    r"(?:cree[rz]?|creons|construi\w*|fais|faire|faites|ajoute[rz]?|prepare[rz]?) (?:moi |nous )?(?:un|1) "
    r"(?:nouveau |nouvel |autre )?modules?\b",
    r"(?<!au )(?<!du )(?<!ce )(?<!le )(?:nouveau|nouvel) modules?\b",
    r"creation (?:d un |de |du )?modules?\b", r"cree[rz]? (?:de |des |du )?modules?\b")
SOFT_CREATE = any_of(
    r"(?:je veux|je voudrais|j aimerais|je souhaite|on veut|il faut) (?:suivre|gerer)\b",
    r"(?:suivre|gerer) (?:mes|nos)\b")
