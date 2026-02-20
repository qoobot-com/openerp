declare module 'nprogress' {
  interface NProgress {
    start(): NProgress
    done(force?: boolean): NProgress
    inc(amount?: number): NProgress
    configure(options: NProgressOptions): NProgress
    set(number: number): NProgress
    status: number | null
  }

  interface NProgressOptions {
    minimum?: number
    template?: string
    easing?: string
    speed?: number
    trickle?: boolean
    trickleSpeed?: number
    showSpinner?: boolean
    parent?: string
    barSelector?: string
    spinnerSelector?: string
  }

  const nprogress: NProgress
  export default nprogress
}
