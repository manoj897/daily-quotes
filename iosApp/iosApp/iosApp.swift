import SwiftUI
import composeApp

@main
struct iosApp: App {
    
    init() {
        EntryKt.initKoinIos()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
