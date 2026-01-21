\# 🎤 English Voice Tutor



App Android para practicar inglés conversacional con Ollama (local) y funciones de voz.



\## 📱 Capturas de Pantalla



\### Conversación con voz

!\[Conversación con voz](screenshots/voice\_conversation.jpg)



\### Traducción instantánea

!\[Traducción](screenshots/translation\_feature.jpg)



\### Pantalla principal

!\[Pantalla principal](screenshots/home\_screen.jpg)



\## ✨ Características



\- \*\*Reconocimiento de voz\*\*: Habla en español, responde en inglés

\- \*\*Síntesis de voz\*\*: Escucha las respuestas del modelo

\- \*\*Traducción contextual\*\*: Toque para ver traducción al español

\- \*\*Modelo local\*\*: Llama 3.1 8B vía Ollama (sin internet necesaria)



\## 🛠️ Requisitos



\- \*\*Ollama\*\* corriendo con `llama3.1:8b` instalado

\- \*\*Configurar IP\*\*: En `gradle.properties` global (ver instrucciones abajo)



\## ⚙️ Configuración



1\. Crea un archivo `~/.gradle/gradle.properties` con tu IP de Ollama:

&nbsp;  ```properties

&nbsp;  ollamaUrl=http://TU\_IP\_LOCAL:11434/

