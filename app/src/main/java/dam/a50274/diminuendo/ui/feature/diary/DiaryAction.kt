package dam.a50274.diminuendo.ui.feature.diary

sealed interface DiaryAction {
    data class Delete(val id: String) : DiaryAction
    object Refresh : DiaryAction
    object InsertDebugEntry : DiaryAction
}
