&lt;!-- markdownlint-disable MD033 MD041 --&gt;
&lt;p align="center"&gt;
  &lt;img src="screenshots/voice_conversation.jpg" width="200" alt="Logo" style="border-radius: 15px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);"&gt;
&lt;/p&gt;

&lt;h1 align="center"&gt;🎤 English Voice Tutor&lt;/h1&gt;

&lt;p align="center"&gt;
  &lt;strong&gt;App Android para practicar inglés conversacional con IA local&lt;/strong&gt;
&lt;/p&gt;

&lt;p align="center"&gt;
  &lt;a href="https://github.com/gusrivera1981/EnglishTutor/stargazers"&gt;
    &lt;img src="https://img.shields.io/github/stars/gusrivera1981/EnglishTutor?style=flat-square" alt="Stars"&gt;
  &lt;/a&gt;
  &lt;a href="https://github.com/gusrivera1981/EnglishTutor/issues"&gt;
    &lt;img src="https://img.shields.io/github/issues/gusrivera1981/EnglishTutor?style=flat-square" alt="Issues"&gt;
  &lt;/a&gt;
  &lt;a href="https://github.com/gusrivera1981/EnglishTutor/blob/main/LICENSE"&gt;
    &lt;img src="https://img.shields.io/github/license/gusrivera1981/EnglishTutor?style=flat-square" alt="License"&gt;
  &lt;/a&gt;
&lt;/p&gt;

---

## 📱 Capturas de Pantalla

&lt;table align="center"&gt;
  &lt;tr&gt;
    &lt;td align="center" width="33%"&gt;
      &lt;img src="screenshots/voice_conversation.jpg" width="250" style="border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);" alt="Conversación con voz"&gt;
      &lt;br&gt;
      &lt;strong&gt;🎤 Conversación con voz&lt;/strong&gt;
    &lt;/td&gt;
    &lt;td align="center" width="33%"&gt;
      &lt;img src="screenshots/translation_feature.jpg" width="250" style="border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);" alt="Traducción instantánea"&gt;
      &lt;br&gt;
      &lt;strong&gt;🌐 Traducción instantánea&lt;/strong&gt;
    &lt;/td&gt;
    &lt;td align="center" width="33%"&gt;
      &lt;img src="screenshots/home_screen.jpg" width="250" style="border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);" alt="Pantalla principal"&gt;
      &lt;br&gt;
      &lt;strong&gt;🏠 Pantalla principal&lt;/strong&gt;
    &lt;/td&gt;
  &lt;/tr&gt;
&lt;/table&gt;

---

## ✨ Características

- **🎙️ Reconocimiento de voz**: Habla en español, responde en inglés
- **🔊 Síntesis de voz**: Escucha las respuestas del modelo
- **🌐 Traducción contextual**: Toque para ver traducción al español
- **🤖 Modelo local**: Llama 3.1 8B vía Ollama (sin internet necesaria)
- **📱 UI moderna**: Jetpack Compose con Material 3
- **⚡ Respuestas rápidas**: Coroutines y Flow para performance

---

## 🛠️ Requisitos

### 📋 Prerrequisitos

- **Android Studio** (Hedgehog o superior)
- **Ollama** instalado y corriendo localmente
- **Modelo**: `llama3.1:8b` descargado (`ollama run llama3.1:8b`)

### 🔧 Configuración Ollama

1. **Instala Ollama**: https://ollama.ai/
2. **Descarga el modelo**:
   ```bash
   ollama run llama3.1:8b