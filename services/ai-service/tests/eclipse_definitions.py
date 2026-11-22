LOCATIONS = {"name": "Locations", "icon": "package",
             "columns": [{"key": "materiel", "label": "Matériel", "type": "TEXT", "required": True},
                         {"key": "caution", "label": "Caution", "type": "MONEY", "required": False}],
             "statuses": [{"key": "sortie", "label": "Sortie", "tone": "INFO", "final": False},
                          {"key": "rendue", "label": "Rendue", "tone": "SUCCESS", "final": True}],
             "indicators": [{"key": "sorties", "label": "Sorties", "icon": "clock", "kind": "COUNT",
                             "statuses": ["sortie"]}]}
