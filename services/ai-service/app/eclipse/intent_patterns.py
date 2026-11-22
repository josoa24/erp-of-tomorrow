from __future__ import annotations

from app.eclipse.text import any_of

ADD_VERBS = any_of(r"ajout\w*", r"rajout\w*", r"activ(?:e|er|ez)\b", r"mets?\b", r"mettre\b", r"integr\w*")
QUESTION_WORDS = any_of(
    r"combien\b", r"quel(?:le)?s?\b", r"qui\b", r"quand\b", r"pourquoi\b", r"comment\b", r"liste[rz]?\b",
    r"montre[rz]?\b", r"affiche[rz]?\b", r"donne[rz]?\b", r"cherche[rz]?\b", r"trouve[rz]?\b",
    r"est ce qu", r"y a t il\b", r"qu est ce\b", r"savoir\b", r"connaitre\b")
EXPLICIT_CREATE = any_of(
    r"(?:cree[rz]?|creons|construi\w*|fais|faire|faites|ajoute[rz]?|prepare[rz]?) (?:moi |nous )?(?:un|1) "
    r"(?:nouveau |nouvel |autre )?modules?\b",
    r"(?<!au )(?<!du )(?<!ce )(?<!le )(?:nouveau|nouvel) modules?\b",
    r"creation (?:d un |de |du )?modules?\b", r"cree[rz]? (?:de |des |du )?modules?\b")
SOFT_CREATE = any_of(
    r"(?:je veux|je voudrais|j aimerais|je souhaite|on veut|il faut) (?:suivre|gerer)\b",
    r"(?:suivre|gerer) (?:mes|nos)\b")
