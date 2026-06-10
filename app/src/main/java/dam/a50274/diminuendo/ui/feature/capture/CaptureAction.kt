package dam.a50274.diminuendo.ui.feature.capture

sealed interface CaptureAction {
    object ToggleRecording : CaptureAction
    object SaveMeasurement : CaptureAction
    object AcknowledgeError : CaptureAction
}
