# 📲 Practica7Nativas

Aplicación Android nativa desarrollada con Kotlin y Jetpack Compose que permite administrar usuarios, enviar notificaciones push mediante Firebase y consultar registros.

## 🚀 Características

- 🔐 Autenticación de usuarios
- 🗂️ Gestión de perfiles (nombre, correo, foto, etc.)
- 🧾 Registro y consulta de usuarios almacenados en Firestore
- 📤 Pantalla para enviar notificaciones push a usuarios seleccionados
- 📥 Visualización del historial de notificaciones recibidas por el usuario
- 🎨 Diseño moderno con Material 3 y esquinas redondeadas

> 🛠️ **Las funcionalidades relacionadas con notificaciones push aún están en proceso de integración y pruebas.** Esto incluye:
> - Visualización automática de notificaciones recibidas
> - Almacenamiento completo y seguro del historial
> - Confirmación visual del estado de envío

## 🧩 Tecnologías utilizadas

- Kotlin + Jetpack Compose
- Firebase (Authentication, Firestore, Cloud Functions, Cloud Messaging)
- Material 3 Design
- Android API 24+

## 🔧 Estructura del proyecto
📁 app/    
  ├── activities/
  
  │ └── NotificationSenderScreen.kt
  
  ├── models/
  
  │ └── UserOption.kt
  
  ├── firebase/
  
  │ └── index.js (funciones Cloud Functions)
  
  └── res/
  
  └── layout, themes, drawables


 > ** Link del funcionamiento: https://youtube.com/shorts/9zrMdjYu58M?feature=share







